package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import com.nutrigo.backend.domain.model.ResultadoImc;

import java.math.BigDecimal;

public record ImcResponse(BigDecimal valor, String categoria) {

    public static ImcResponse desde(ResultadoImc resultado) {
        return new ImcResponse(resultado.valor(), resultado.categoria().name());
    }
}
