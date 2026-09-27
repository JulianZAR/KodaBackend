package com.nutrigo.backend.infrastructure.adapter.out.persistence;

import com.nutrigo.backend.application.port.out.RegistroProgresoRepositoryPort;
import com.nutrigo.backend.domain.model.RegistroProgreso;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RegistroProgresoRepositoryAdapter implements RegistroProgresoRepositoryPort {

    private final RegistroProgresoJpaRepository registroProgresoJpaRepository;

    public RegistroProgresoRepositoryAdapter(RegistroProgresoJpaRepository registroProgresoJpaRepository) {
        this.registroProgresoJpaRepository = registroProgresoJpaRepository;
    }

    @Override
    public RegistroProgreso guardar(RegistroProgreso registro) {
        return registroProgresoJpaRepository.save(registro);
    }

    @Override
    public List<RegistroProgreso> buscarPorUsuarioOrdenadoDesc(UUID usuarioId) {
        return registroProgresoJpaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }
}
