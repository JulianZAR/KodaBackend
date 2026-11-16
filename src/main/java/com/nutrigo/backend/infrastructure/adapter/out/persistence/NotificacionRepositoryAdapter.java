package com.nutrigo.backend.infrastructure.adapter.out.persistence;

import com.nutrigo.backend.application.port.out.NotificacionRepositoryPort;
import com.nutrigo.backend.domain.model.Notificacion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class NotificacionRepositoryAdapter implements NotificacionRepositoryPort {

    private final NotificacionJpaRepository notificacionJpaRepository;

    public NotificacionRepositoryAdapter(NotificacionJpaRepository notificacionJpaRepository) {
        this.notificacionJpaRepository = notificacionJpaRepository;
    }

    @Override
    public Notificacion guardar(Notificacion notificacion) {
        return notificacionJpaRepository.save(notificacion);
    }

    @Override
    public List<Notificacion> buscarRecientesPorUsuario(UUID usuarioId) {
        return notificacionJpaRepository.findTop50ByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    @Override
    public Optional<Notificacion> buscarDeUsuario(UUID notificacionId, UUID usuarioId) {
        return notificacionJpaRepository.findByIdAndUsuarioId(notificacionId, usuarioId);
    }

    @Override
    public void marcarTodasLeidas(UUID usuarioId) {
        notificacionJpaRepository.marcarTodasLeidas(usuarioId);
    }
}
