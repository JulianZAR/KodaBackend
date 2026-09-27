package com.nutrigo.backend.application.port.in;

import com.nutrigo.backend.domain.model.ResultadoImc;

import java.math.BigDecimal;

public interface CalcularImcUseCase {

    ResultadoImc calcular(BigDecimal pesoKg, BigDecimal estaturaCm);
}
