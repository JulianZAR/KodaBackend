package com.nutrigo.backend.domain.service;

import com.nutrigo.backend.domain.model.Meta;
import com.nutrigo.backend.domain.model.PlanNutricional;
import com.nutrigo.backend.domain.model.RegistroProgreso;
import com.nutrigo.backend.domain.model.Usuario;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GeneradorContenidoReporteTest {

    @Test
    void incluyeImcPlanYCantidadDeRegistrosCuandoTodoEstaCompleto() {
        Usuario usuario = new Usuario(UUID.randomUUID(), "ana@nutrigo.local", "Ana");
        usuario.actualizarEstaturaYMeta(BigDecimal.valueOf(170), Meta.PERDER_PESO);
        usuario.actualizarPeso(BigDecimal.valueOf(70));
        PlanNutricional plan = new PlanNutricional(usuario, "Plan fase 1", Meta.PERDER_PESO, "Detalle");
        RegistroProgreso registro = new RegistroProgreso(usuario, BigDecimal.valueOf(70), BigDecimal.valueOf(24.22));

        String contenido = GeneradorContenidoReporte.generar(usuario, List.of(registro), plan);

        assertThat(contenido)
                .contains("Ana")
                .contains("IMC: 24.22")
                .contains("Plan fase 1")
                .contains("Registros de progreso: 1");
    }

    @Test
    void indicaPerfilIncompletoCuandoFaltaPesoOEstatura() {
        Usuario usuario = new Usuario(UUID.randomUUID(), "ana@nutrigo.local", "Ana");

        String contenido = GeneradorContenidoReporte.generar(usuario, List.of(), null);

        assertThat(contenido)
                .contains("no disponible (perfil incompleto)")
                .contains("sin plan asignado")
                .contains("Registros de progreso: 0");
    }
}
