package com.nutrigo.backend.application.port.out;

import com.nutrigo.backend.domain.model.PlanNutricional;

import java.util.Optional;
import java.util.UUID;

public interface PlanNutricionalRepositoryPort {

    Optional<PlanNutricional> buscarPorUsuarioId(UUID usuarioId);

    PlanNutricional guardar(PlanNutricional plan);
}
