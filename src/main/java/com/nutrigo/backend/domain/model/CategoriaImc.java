package com.nutrigo.backend.domain.model;

import java.math.BigDecimal;

public enum CategoriaImc {
    BAJO_PESO,
    NORMAL,
    SOBREPESO,
    OBESIDAD;

    private static final BigDecimal LIMITE_BAJO_PESO = BigDecimal.valueOf(18.5);
    private static final BigDecimal LIMITE_NORMAL = BigDecimal.valueOf(25);
    private static final BigDecimal LIMITE_SOBREPESO = BigDecimal.valueOf(30);

    public static CategoriaImc desde(BigDecimal imc) {
        if (imc.compareTo(LIMITE_BAJO_PESO) < 0) {
            return BAJO_PESO;
        }
        if (imc.compareTo(LIMITE_NORMAL) < 0) {
            return NORMAL;
        }
        if (imc.compareTo(LIMITE_SOBREPESO) < 0) {
            return SOBREPESO;
        }
        return OBESIDAD;
    }
}
