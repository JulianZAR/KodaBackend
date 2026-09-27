package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import com.nutrigo.backend.domain.model.Meta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ActualizarPerfilRequest(

        @NotNull
        @Schema(example = "170.0", description = "Estatura en centímetros")
        BigDecimal estaturaCm,

        @NotNull
        @Schema(example = "68.5", description = "Peso en kilogramos")
        BigDecimal pesoKg,

        @Schema(description = "Meta nutricional del usuario")
        Meta meta
) {
}
