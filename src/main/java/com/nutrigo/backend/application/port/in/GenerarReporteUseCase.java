package com.nutrigo.backend.application.port.in;

import com.nutrigo.backend.domain.model.Reporte;

import java.util.UUID;

public interface GenerarReporteUseCase {

    /** El propio usuario genera su reporte. */
    Reporte generarReporte(UUID usuarioId);

    /** Un NUTRITIONIST o ADMIN genera el reporte de un paciente; se le notifica. */
    Reporte generarReporteParaPaciente(UUID usuarioId);
}
