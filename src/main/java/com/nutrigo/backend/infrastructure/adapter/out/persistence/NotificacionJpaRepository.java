package com.nutrigo.backend.infrastructure.adapter.out.persistence;

import com.nutrigo.backend.domain.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificacionJpaRepository extends JpaRepository<Notificacion, UUID> {

    List<Notificacion> findTop50ByUsuarioIdOrderByFechaDesc(UUID usuarioId);

    Optional<Notificacion> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    @Modifying
    @Query("update Notificacion n set n.leida = true where n.usuario.id = :usuarioId and n.leida = false")
    int marcarTodasLeidas(@Param("usuarioId") UUID usuarioId);
}
