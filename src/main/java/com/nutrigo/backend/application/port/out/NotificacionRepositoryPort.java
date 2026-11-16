package com.nutrigo.backend.application.port.out;

import com.nutrigo.backend.domain.model.Notificacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificacionRepositoryPort {

    Notificacion guardar(Notificacion notificacion);

    /** Las más recientes primero, hasta un máximo de 50. */
    List<Notificacion> buscarRecientesPorUsuario(UUID usuarioId);

    Optional<Notificacion> buscarDeUsuario(UUID notificacionId, UUID usuarioId);

    void marcarTodasLeidas(UUID usuarioId);
}
