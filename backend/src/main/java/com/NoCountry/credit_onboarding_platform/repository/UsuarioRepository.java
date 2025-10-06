package com.NoCountry.credit_onboarding_platform.repository;

import com.NoCountry.credit_onboarding_platform.model.Usuario;
import com.NoCountry.credit_onboarding_platform.model.Usuario.Rol;
import com.NoCountry.credit_onboarding_platform.model.Usuario.EstadoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Buscar por email
    Optional<Usuario> findByEmail(String email);
    
    // Verificar si existe email
    boolean existsByEmail(String email);
    
    // Buscar por RFC
    Optional<Usuario> findByRfc(String rfc);
    
    // Buscar por rol
    List<Usuario> findByRol(Rol rol);
    
    // Buscar por estado
    List<Usuario> findByEstado(EstadoUsuario estado);
    
    // Buscar clientes activos
    List<Usuario> findByRolAndEstado(Rol rol, EstadoUsuario estado);
    
    // Buscar por nombre (búsqueda parcial)
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    
    // Buscar por razón social
    List<Usuario> findByRazonSocialContainingIgnoreCase(String razonSocial);
    
    // Query personalizado: buscar admins
    @Query("SELECT u FROM Usuario u WHERE u.rol = 'ADMIN' AND u.estado = 'ACTIVO'")
    List<Usuario> findAdministradoresActivos();
}