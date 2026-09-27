package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import com.nutrigo.backend.domain.model.RegistroProgreso;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProgresoResponse(UUID id, BigDecimal pesoKg, BigDecimal imc, Instant fecha) {

    public static ProgresoResponse desde(RegistroProgreso registro) {
        return new ProgresoResponse(registro.getId(), registro.getPesoKg(), registro.getImc(), registro.getFecha());
    }
}
