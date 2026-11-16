package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import com.nutrigo.backend.domain.model.Notificacion;

import java.time.Instant;
import java.util.UUID;

public record NotificacionResponse(UUID id, String tipo, String titulo, String mensaje, boolean leida, Instant fecha) {

    public static NotificacionResponse desde(Notificacion n) {
        return new NotificacionResponse(n.getId(), n.getTipo().name(), n.getTitulo(), n.getMensaje(), n.isLeida(),
                n.getFecha());
    }
}
