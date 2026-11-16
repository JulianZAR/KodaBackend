package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import com.nutrigo.backend.application.port.in.ConsultarPacientesUseCase.ResumenPaciente;
import com.nutrigo.backend.domain.model.Meta;
import com.nutrigo.backend.domain.model.Usuario;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Paciente visto por un NUTRITIONIST o ADMIN: perfil, IMC actual y plan vigente. */
public record PacienteResponse(
        UUID id,
        String nombre,
        String email,
        BigDecimal estaturaCm,
        BigDecimal pesoKg,
        Meta meta,
        BigDecimal imc,
        String categoriaImc,
        PlanResponse plan,
        Instant fechaRegistro
) {

    public static PacienteResponse desde(ResumenPaciente resumen) {
        Usuario u = resumen.usuario();
        return new PacienteResponse(
                u.getId(),
                u.getNombre(),
                u.getEmail(),
                u.getEstaturaCm(),
                u.getPesoKg(),
                u.getMeta(),
                resumen.imc() != null ? resumen.imc().valor() : null,
                resumen.imc() != null ? resumen.imc().categoria().name() : null,
                resumen.planVigente() != null ? PlanResponse.desde(resumen.planVigente()) : null,
                u.getFechaRegistro());
    }
}
