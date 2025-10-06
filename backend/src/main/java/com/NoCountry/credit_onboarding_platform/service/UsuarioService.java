package com.NoCountry.credit_onboarding_platform.service;

import com.NoCountry.credit_onboarding_platform.model.Usuario;
import com.NoCountry.credit_onboarding_platform.model.Usuario.Rol;
import com.NoCountry.credit_onboarding_platform.model.Usuario.EstadoUsuario;
import com.NoCountry.credit_onboarding_platform.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // ========== CRUD Básico ==========
    
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public Usuario save(Usuario usuario) {
        // Encriptar contraseña si es nueva
        if (usuario.getId() == null || !usuario.getPassword().startsWith("$2a$")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }
    
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    // ========== Métodos de Negocio ==========
    
    public Usuario registrarCliente(Usuario cliente) {
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(cliente.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Validar RFC único para clientes
        if (cliente.getRfc() != null && usuarioRepository.findByRfc(cliente.getRfc()).isPresent()) {
            throw new RuntimeException("El RFC ya está registrado");
        }
        
        cliente.setRol(Rol.CLIENTE);
        cliente.setEstado(EstadoUsuario.ACTIVO);
        return save(cliente);
    }
    
    public Usuario registrarAdministrador(Usuario admin) {
        if (usuarioRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        admin.setRol(Rol.ADMIN);
        admin.setEstado(EstadoUsuario.ACTIVO);
        return save(admin);
    }
    
    public Optional<Usuario> login(String email, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        
        if (usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getPassword())) {
            if (usuario.get().getEstado() != EstadoUsuario.ACTIVO) {
                throw new RuntimeException("Usuario inactivo o bloqueado");
            }
            return usuario;
        }
        
        return Optional.empty();
    }
    
    public Usuario cambiarEstado(Long usuarioId, EstadoUsuario nuevoEstado) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setEstado(nuevoEstado);
        return usuarioRepository.save(usuario);
    }
    
    public Usuario actualizarPerfil(Long usuarioId, Usuario datosActualizados) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setNombre(datosActualizados.getNombre());
        usuario.setTelefono(datosActualizados.getTelefono());
        
        if (usuario.getRol() == Rol.CLIENTE) {
            usuario.setRazonSocial(datosActualizados.getRazonSocial());
            usuario.setDireccion(datosActualizados.getDireccion());
            usuario.setRepresentanteLegal(datosActualizados.getRepresentanteLegal());
        }
        
        return usuarioRepository.save(usuario);
    }
    
    public List<Usuario> getClientes() {
        return usuarioRepository.findByRol(Rol.CLIENTE);
    }
    
    public List<Usuario> getAdministradores() {
        return usuarioRepository.findByRol(Rol.ADMIN);
    }
    
    public List<Usuario> getClientesActivos() {
        return usuarioRepository.findByRolAndEstado(Rol.CLIENTE, EstadoUsuario.ACTIVO);
    }
    
    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}