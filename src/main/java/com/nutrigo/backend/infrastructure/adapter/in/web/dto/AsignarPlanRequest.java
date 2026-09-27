package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import com.nutrigo.backend.domain.model.Meta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AsignarPlanRequest(

        @NotBlank
        @Schema(example = "Plan pérdida de peso - fase 1")
        String nombre,

        @NotNull
        Meta objetivo,

        @Schema(description = "Detalle del plan: alimentos, suplementos, pautas")
        String descripcion
) {
}
