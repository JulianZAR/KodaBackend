package com.nutrigo.backend.infrastructure.adapter.out.persistence;

import com.nutrigo.backend.application.port.out.PlanNutricionalRepositoryPort;
import com.nutrigo.backend.domain.model.PlanNutricional;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PlanNutricionalRepositoryAdapter implements PlanNutricionalRepositoryPort {

    private final PlanNutricionalJpaRepository planNutricionalJpaRepository;

    public PlanNutricionalRepositoryAdapter(PlanNutricionalJpaRepository planNutricionalJpaRepository) {
        this.planNutricionalJpaRepository = planNutricionalJpaRepository;
    }

    @Override
    public Optional<PlanNutricional> buscarPorUsuarioId(UUID usuarioId) {
        return planNutricionalJpaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public PlanNutricional guardar(PlanNutricional plan) {
        return planNutricionalJpaRepository.save(plan);
    }
}
