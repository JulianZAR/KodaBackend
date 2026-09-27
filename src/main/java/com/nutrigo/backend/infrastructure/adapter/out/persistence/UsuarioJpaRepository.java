package com.nutrigo.backend.infrastructure.adapter.out.persistence;

import com.nutrigo.backend.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByKeycloakUserId(UUID keycloakUserId);
}
