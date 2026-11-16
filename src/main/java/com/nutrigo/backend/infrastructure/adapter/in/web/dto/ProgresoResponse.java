package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import com.nutrigo.backend.domain.model.CategoriaImc;
import com.nutrigo.backend.domain.model.RegistroProgreso;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProgresoResponse(UUID id, BigDecimal pesoKg, BigDecimal imc, String categoria, Instant fecha) {

    public static ProgresoResponse desde(RegistroProgreso registro) {
        return new ProgresoResponse(
                registro.getId(),
                registro.getPesoKg(),
                registro.getImc(),
                CategoriaImc.desde(registro.getImc()).name(),
                registro.getFecha());
    }
}
