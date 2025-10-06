package com.NoCountry.credit_onboarding_platform.service;

import com.NoCountry.credit_onboarding_platform.model.Documento;
import com.NoCountry.credit_onboarding_platform.model.Documento.TipoDocumento;
import com.NoCountry.credit_onboarding_platform.model.Documento.EstadoDocumento;
import com.NoCountry.credit_onboarding_platform.repository.DocumentoRepository;
import com.NoCountry.credit_onboarding_platform.repository.SolicitudRepository;
import com.NoCountry.credit_onboarding_platform.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DocumentoService {
    
    @Autowired
    private DocumentoRepository documentoRepository;
    
    @Autowired
    private SolicitudRepository solicitudRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    // ========== CRUD Básico ==========
    
    public List<Documento> findAll() {
        return documentoRepository.findAll();
    }
    
    public Optional<Documento> findById(Long id) {
        return documentoRepository.findById(id);
    }
    
    public Documento save(Documento documento) {
        return documentoRepository.save(documento);
    }
    
    public void deleteById(Long id) {
        documentoRepository.deleteById(id);
    }
    
    // ========== Métodos de Negocio ==========
    
    public Documento subirDocumento(MultipartFile file, TipoDocumento tipo, Long solicitudId, Long clienteId) throws IOException {
        // Validar archivo
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }
        
        // Generar nombre único
        String nombreOriginal = file.getOriginalFilename();
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        String nombreUnico = UUID.randomUUID().toString() + extension;
        
        // Crear directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Guardar archivo
        Path filePath = uploadPath.resolve(nombreUnico);
        Files.copy(file.getInputStream(), filePath);
        
        // Buscar entidades relacionadas
        var solicitud = solicitudId != null ? 
            solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada")) : null;
        
        var cliente = clienteId != null ? 
            usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado")) : null;
        
        // Crear entidad con todas las relaciones
        Documento documento = Documento.builder()
            .tipoDocumento(tipo)
            .nombreArchivo(nombreOriginal)
            .rutaArchivo(filePath.toString())
            .tamanoBytes(file.getSize())
            .estado(EstadoDocumento.PENDIENTE)
            .solicitud(solicitud)
            .cliente(cliente)
            .build();
        
        return documentoRepository.save(documento);
    }
    
    public Documento validarDocumento(Long documentoId, EstadoDocumento nuevoEstado, String comentario) {
        Documento documento = documentoRepository.findById(documentoId)
            .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        
        documento.setEstado(nuevoEstado);
        documento.setComentarioValidacion(comentario);
        
        return documentoRepository.save(documento);
    }
    
    public List<Documento> getDocumentosPorSolicitud(Long solicitudId) {
        return documentoRepository.findBySolicitudId(solicitudId);
    }
    
    public List<Documento> getDocumentosKYCPorCliente(Long clienteId) {
        return documentoRepository.findDocumentosKYCByCliente(clienteId);
    }
    
    public List<Documento> getDocumentosPendientes() {
        return documentoRepository.findByEstadoOrderByFechaCargaDesc(EstadoDocumento.PENDIENTE);
    }
    
    public boolean solicitudTieneDocumentosCompletos(Long solicitudId) {
        Long pendientes = documentoRepository.countBySolicitudIdAndEstado(
            solicitudId, 
            EstadoDocumento.PENDIENTE
        );
        
        Long validados = documentoRepository.countBySolicitudIdAndEstado(
            solicitudId, 
            EstadoDocumento.VALIDADO
        );
        
        return pendientes == 0 && validados > 0;
    }
    
    public boolean clienteTieneKYCCompleto(Long clienteId) {
        List<Documento> docsKYC = documentoRepository.findDocumentosKYCByCliente(clienteId);
        
        if (docsKYC.isEmpty()) {
            return false;
        }
        
        // Verificar que todos estén validados
        return docsKYC.stream()
            .allMatch(doc -> doc.getEstado() == EstadoDocumento.VALIDADO);
    }
    
    public void eliminarDocumento(Long documentoId) throws IOException {
        Documento documento = documentoRepository.findById(documentoId)
            .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        
        // Eliminar archivo físico
        Path filePath = Paths.get(documento.getRutaArchivo());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        
        documentoRepository.deleteById(documentoId);
    }
}