package com.nutrigo.backend.domain.service;

import com.nutrigo.backend.domain.model.CategoriaImc;
import com.nutrigo.backend.domain.model.PlanNutricional;
import com.nutrigo.backend.domain.model.RegistroProgreso;
import com.nutrigo.backend.domain.model.Usuario;

import java.math.BigDecimal;
import java.util.List;

/**
 * Construye el contenido de texto de un reporte a partir del estado actual
 * del usuario, su historial de progreso y su plan vigente. Lógica pura, sin
 * Spring, para poder probarse con JUnit puro.
 */
public final class GeneradorContenidoReporte {

    private GeneradorContenidoReporte() {
    }

    public static String generar(Usuario usuario, List<RegistroProgreso> historial, PlanNutricional plan) {
        StringBuilder contenido = new StringBuilder();
        contenido.append("Reporte nutricional de ").append(usuario.getNombre()).append(System.lineSeparator());
        contenido.append("Estatura: ").append(formatear(usuario.getEstaturaCm(), "cm"))
                .append(" | Peso actual: ").append(formatear(usuario.getPesoKg(), "kg"))
                .append(System.lineSeparator());
        contenido.append("IMC: ").append(describirImc(usuario)).append(System.lineSeparator());
        contenido.append("Meta: ").append(usuario.getMeta() != null ? usuario.getMeta() : "sin definir")
                .append(System.lineSeparator());
        contenido.append("Plan vigente: ").append(describirPlan(plan)).append(System.lineSeparator());
        contenido.append("Registros de progreso: ").append(historial.size());
        return contenido.toString();
    }

    private static String describirImc(Usuario usuario) {
        if (!usuario.tieneDatosFisicosCompletos()) {
            return "no disponible (perfil incompleto)";
        }
        BigDecimal imc = CalculadoraIMC.calcular(usuario.getPesoKg(), usuario.getEstaturaCm());
        return imc + " (" + CategoriaImc.desde(imc) + ")";
    }

    private static String describirPlan(PlanNutricional plan) {
        return plan != null ? plan.getNombre() + " (" + plan.getObjetivo() + ")" : "sin plan asignado";
    }

    private static String formatear(BigDecimal valor, String unidad) {
        return valor != null ? valor + " " + unidad : "no registrado";
    }
}
