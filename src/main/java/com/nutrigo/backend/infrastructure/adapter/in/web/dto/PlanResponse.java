package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import com.nutrigo.backend.domain.model.Meta;
import com.nutrigo.backend.domain.model.PlanNutricional;

import java.time.Instant;
import java.util.UUID;

public record PlanResponse(UUID id, String nombre, Meta objetivo, String descripcion, Instant fechaAsignacion) {

    public static PlanResponse desde(PlanNutricional plan) {
        return new PlanResponse(plan.getId(), plan.getNombre(), plan.getObjetivo(), plan.getDescripcion(),
                plan.getFechaAsignacion());
    }
}
