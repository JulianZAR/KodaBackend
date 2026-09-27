package com.nutrigo.backend.application.port.in;

import com.nutrigo.backend.domain.model.PlanNutricional;

import java.util.Optional;
import java.util.UUID;

public interface ConsultarPlanUseCase {

    Optional<PlanNutricional> consultarPlan(UUID usuarioId);
}
