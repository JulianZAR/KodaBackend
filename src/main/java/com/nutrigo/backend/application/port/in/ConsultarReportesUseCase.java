package com.nutrigo.backend.application.port.in;

import com.nutrigo.backend.domain.model.Reporte;

import java.util.List;
import java.util.UUID;

public interface ConsultarReportesUseCase {

    List<Reporte> consultarReportes(UUID usuarioId);
}
