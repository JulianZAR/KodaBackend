package com.nutrigo.backend.domain.service;

import com.nutrigo.backend.domain.exception.DatosInvalidosException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Lógica pura del cálculo de IMC. Sin dependencias de Spring para poder
 * probarse con JUnit puro.
 */
public final class CalculadoraIMC {

    private static final BigDecimal PESO_MINIMO_KG = BigDecimal.ONE;
    private static final BigDecimal PESO_MAXIMO_KG = BigDecimal.valueOf(500);
    private static final BigDecimal ESTATURA_MINIMA_CM = BigDecimal.valueOf(30);
    private static final BigDecimal ESTATURA_MAXIMA_CM = BigDecimal.valueOf(250);
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private CalculadoraIMC() {
    }

    public static BigDecimal calcular(BigDecimal pesoKg, BigDecimal estaturaCm) {
        validar(pesoKg, estaturaCm);
        BigDecimal estaturaM = estaturaCm.divide(CIEN, 4, RoundingMode.HALF_UP);
        BigDecimal estaturaAlCuadrado = estaturaM.multiply(estaturaM);
        return pesoKg.divide(estaturaAlCuadrado, 2, RoundingMode.HALF_UP);
    }

    public static void validar(BigDecimal pesoKg, BigDecimal estaturaCm) {
        validarPeso(pesoKg);
        validarEstatura(estaturaCm);
    }

    public static void validarPeso(BigDecimal pesoKg) {
        if (pesoKg == null) {
            throw new DatosInvalidosException("El peso es obligatorio.");
        }
        if (pesoKg.compareTo(PESO_MINIMO_KG) < 0 || pesoKg.compareTo(PESO_MAXIMO_KG) > 0) {
            throw new DatosInvalidosException(
                    "El peso debe estar entre " + PESO_MINIMO_KG + " y " + PESO_MAXIMO_KG + " kg.");
        }
    }

    public static void validarEstatura(BigDecimal estaturaCm) {
        if (estaturaCm == null) {
            throw new DatosInvalidosException("La estatura es obligatoria.");
        }
        if (estaturaCm.compareTo(ESTATURA_MINIMA_CM) < 0 || estaturaCm.compareTo(ESTATURA_MAXIMA_CM) > 0) {
            throw new DatosInvalidosException(
                    "La estatura debe estar entre " + ESTATURA_MINIMA_CM + " y " + ESTATURA_MAXIMA_CM + " cm.");
        }
    }
}
