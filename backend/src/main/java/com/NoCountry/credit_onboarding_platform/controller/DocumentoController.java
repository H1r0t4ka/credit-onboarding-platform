package com.NoCountry.credit_onboarding_platform.controller;

import com.NoCountry.credit_onboarding_platform.model.Documento;
import com.NoCountry.credit_onboarding_platform.model.Documento.TipoDocumento;
import com.NoCountry.credit_onboarding_platform.model.Documento.EstadoDocumento;
import com.NoCountry.credit_onboarding_platform.service.DocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/documentos")
@CrossOrigin(origins = "*")
public class DocumentoController {
    
    @Autowired
    private DocumentoService documentoService;
    
    // ========== CRUD Básico ==========
    
    @GetMapping
    public ResponseEntity<List<Documento>> getAllDocumentos() {
        List<Documento> documentos = documentoService.findAll();
        return ResponseEntity.ok(documentos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Documento> getDocumentoById(@PathVariable Long id) {
        Optional<Documento> documento = documentoService.findById(id);
        return documento.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocumento(@PathVariable Long id) {
        try {
            documentoService.eliminarDocumento(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al eliminar el archivo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ========== Endpoints de Negocio ==========
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocumento(
        @RequestParam("file") MultipartFile file,
        @RequestParam("tipo") String tipo,
        @RequestParam(value = "solicitudId", required = false) Long solicitudId,
        @RequestParam(value = "clienteId", required = false) Long clienteId
    ) {
        try {
            TipoDocumento tipoDoc = TipoDocumento.valueOf(tipo);
            Documento documento = documentoService.subirDocumento(file, tipoDoc, solicitudId, clienteId);
            return ResponseEntity.status(HttpStatus.CREATED).body(documento);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al subir el archivo: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Tipo de documento inválido"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/validar")
    public ResponseEntity<?> validarDocumento(
        @PathVariable Long id,
        @RequestBody Map<String, String> body
    ) {
        try {
            EstadoDocumento estado = EstadoDocumento.valueOf(body.get("estado"));
            String comentario = body.get("comentario");
            
            Documento actualizado = documentoService.validarDocumento(id, estado, comentario);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/solicitud/{solicitudId}")
    public ResponseEntity<List<Documento>> getDocumentosPorSolicitud(@PathVariable Long solicitudId) {
        List<Documento> documentos = documentoService.getDocumentosPorSolicitud(solicitudId);
        return ResponseEntity.ok(documentos);
    }
    
    @GetMapping("/cliente/{clienteId}/kyc")
    public ResponseEntity<List<Documento>> getDocumentosKYCPorCliente(@PathVariable Long clienteId) {
        List<Documento> documentos = documentoService.getDocumentosKYCPorCliente(clienteId);
        return ResponseEntity.ok(documentos);
    }
    
    @GetMapping("/pendientes")
    public ResponseEntity<List<Documento>> getDocumentosPendientes() {
        List<Documento> documentos = documentoService.getDocumentosPendientes();
        return ResponseEntity.ok(documentos);
    }
    
    @GetMapping("/solicitud/{solicitudId}/completos")
    public ResponseEntity<Map<String, Boolean>> verificarDocumentosCompletos(@PathVariable Long solicitudId) {
        boolean completos = documentoService.solicitudTieneDocumentosCompletos(solicitudId);
        return ResponseEntity.ok(Map.of("completos", completos));
    }
    
    @GetMapping("/cliente/{clienteId}/kyc-completo")
    public ResponseEntity<Map<String, Boolean>> verificarKYCCompleto(@PathVariable Long clienteId) {
        boolean completo = documentoService.clienteTieneKYCCompleto(clienteId);
        return ResponseEntity.ok(Map.of("kycCompleto", completo));
    }
    
    // ========== Descargar Archivo ==========
    
    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargarDocumento(@PathVariable Long id) {
        try {
            Optional<Documento> docOpt = documentoService.findById(id);
            
            if (!docOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Documento documento = docOpt.get();
            Path filePath = Paths.get(documento.getRutaArchivo());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + documento.getNombreArchivo() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}