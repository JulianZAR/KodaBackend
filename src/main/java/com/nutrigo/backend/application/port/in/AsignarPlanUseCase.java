package com.nutrigo.backend.application.port.in;

import com.nutrigo.backend.domain.model.Meta;
import com.nutrigo.backend.domain.model.PlanNutricional;

import java.util.UUID;

public interface AsignarPlanUseCase {

    PlanNutricional asignarPlan(UUID usuarioId, String nombre, Meta objetivo, String descripcion);
}
