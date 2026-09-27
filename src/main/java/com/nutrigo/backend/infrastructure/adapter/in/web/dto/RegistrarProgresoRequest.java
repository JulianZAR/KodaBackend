package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegistrarProgresoRequest(

        @NotNull
        @Schema(example = "80.2", description = "Peso en kilogramos del nuevo punto de progreso")
        BigDecimal pesoKg
) {
}
