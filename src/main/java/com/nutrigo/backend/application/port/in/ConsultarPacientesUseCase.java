package com.nutrigo.backend.application.port.in;

import com.nutrigo.backend.domain.model.PlanNutricional;
import com.nutrigo.backend.domain.model.RegistroProgreso;
import com.nutrigo.backend.domain.model.ResultadoImc;
import com.nutrigo.backend.domain.model.Usuario;

import java.util.List;
import java.util.UUID;

/** Vista de pacientes para NUTRITIONIST y ADMIN. */
public interface ConsultarPacientesUseCase {

    /** Usuario con su IMC actual y su plan vigente (ambos pueden ser null). */
    record ResumenPaciente(Usuario usuario, ResultadoImc imc, PlanNutricional planVigente) {
    }

    List<ResumenPaciente> listarPacientes();

    ResumenPaciente obtenerPaciente(UUID usuarioId);

    List<RegistroProgreso> consultarHistorialDePaciente(UUID usuarioId);
}
