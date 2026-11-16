package com.nutrigo.backend.infrastructure.adapter.out.persistence;

import com.nutrigo.backend.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByKeycloakUserId(UUID keycloakUserId);

    /** ON CONFLICT: si otra petición ya creó el usuario, no falla ni aborta la transacción. */
    @Modifying
    @Query(value = """
            INSERT INTO usuarios (id, keycloak_user_id, nombre, email, fecha_registro)
            VALUES (:id, :keycloakUserId, :nombre, :email, :fechaRegistro)
            ON CONFLICT DO NOTHING
            """, nativeQuery = true)
    int insertarSiNoExiste(@Param("id") UUID id,
                           @Param("keycloakUserId") UUID keycloakUserId,
                           @Param("nombre") String nombre,
                           @Param("email") String email,
                           @Param("fechaRegistro") Instant fechaRegistro);

    List<Usuario> findAllByOrderByNombreAsc();
}
