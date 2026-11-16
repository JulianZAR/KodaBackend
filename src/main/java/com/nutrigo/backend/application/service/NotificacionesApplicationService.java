package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.in.GestionarNotificacionesUseCase;
import com.nutrigo.backend.application.port.out.NotificacionRepositoryPort;
import com.nutrigo.backend.domain.exception.NotificacionNoEncontradaException;
import com.nutrigo.backend.domain.model.Notificacion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificacionesApplicationService implements GestionarNotificacionesUseCase {

    private final NotificacionRepositoryPort notificacionRepositoryPort;

    public NotificacionesApplicationService(NotificacionRepositoryPort notificacionRepositoryPort) {
        this.notificacionRepositoryPort = notificacionRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notificacion> listar(UUID usuarioId) {
        return notificacionRepositoryPort.buscarRecientesPorUsuario(usuarioId);
    }

    /** Solo puede marcar sus propias notificaciones: una ajena se trata como inexistente. */
    @Override
    public void marcarLeida(UUID usuarioId, UUID notificacionId) {
        Notificacion notificacion = notificacionRepositoryPort.buscarDeUsuario(notificacionId, usuarioId)
                .orElseThrow(() -> new NotificacionNoEncontradaException("La notificación no existe."));
        notificacion.marcarLeida();
        notificacionRepositoryPort.guardar(notificacion);
    }

    @Override
    public void marcarTodasLeidas(UUID usuarioId) {
        notificacionRepositoryPort.marcarTodasLeidas(usuarioId);
    }
}
