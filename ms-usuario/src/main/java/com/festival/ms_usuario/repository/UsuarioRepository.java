package com.festival.ms_usuario.repository;

import com.festival.ms_usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    
    Optional<Usuario> findByEmail(String email);

    
    List<Usuario> findByActivoTrue();

    
    List<Usuario> findByRolNombre(String rolNombre);

    
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = :rol AND u.activo = true")
    List<Usuario> activosPorRol(@Param("rol") String rol);

    
    boolean existsByEmail(String email);
}