package com.nutrigo.backend.domain.service;

import com.nutrigo.backend.domain.exception.DatosInvalidosException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculadoraIMCTest {

    @Test
    void calculaElImcCorrectamente() {
        BigDecimal imc = CalculadoraIMC.calcular(BigDecimal.valueOf(70), BigDecimal.valueOf(175));

        assertThat(imc).isEqualByComparingTo("22.86");
    }

    @Test
    void rechazaPesoCeroONegativo() {
        assertThatThrownBy(() -> CalculadoraIMC.calcular(BigDecimal.ZERO, BigDecimal.valueOf(170)))
                .isInstanceOf(DatosInvalidosException.class);
    }

    @Test
    void rechazaPesoFueraDeRangoRazonable() {
        assertThatThrownBy(() -> CalculadoraIMC.calcular(BigDecimal.valueOf(600), BigDecimal.valueOf(170)))
                .isInstanceOf(DatosInvalidosException.class);
    }

    @Test
    void rechazaEstaturaFueraDeRangoRazonable() {
        assertThatThrownBy(() -> CalculadoraIMC.calcular(BigDecimal.valueOf(70), BigDecimal.valueOf(10)))
                .isInstanceOf(DatosInvalidosException.class);
    }

    @Test
    void rechazaValoresNulos() {
        assertThatThrownBy(() -> CalculadoraIMC.calcular(null, BigDecimal.valueOf(170)))
                .isInstanceOf(DatosInvalidosException.class);
    }
}
