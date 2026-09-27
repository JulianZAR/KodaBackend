package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CalcularImcRequest(

        @NotNull
        @Schema(example = "68.5", description = "Peso en kilogramos")
        BigDecimal pesoKg,

        @NotNull
        @Schema(example = "170.0", description = "Estatura en centímetros")
        BigDecimal estaturaCm
) {
}
