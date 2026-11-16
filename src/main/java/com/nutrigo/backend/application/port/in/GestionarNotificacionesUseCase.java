package com.nutrigo.backend.application.port.in;

import com.nutrigo.backend.domain.model.Notificacion;

import java.util.List;
import java.util.UUID;

public interface GestionarNotificacionesUseCase {

    List<Notificacion> listar(UUID usuarioId);

    void marcarLeida(UUID usuarioId, UUID notificacionId);

    void marcarTodasLeidas(UUID usuarioId);
}
