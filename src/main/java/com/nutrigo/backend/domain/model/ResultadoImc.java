package com.nutrigo.backend.domain.model;

import java.math.BigDecimal;

public record ResultadoImc(BigDecimal valor, CategoriaImc categoria) {
}
