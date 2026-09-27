package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import com.nutrigo.backend.domain.model.Reporte;

import java.time.Instant;
import java.util.UUID;

public record ReporteResponse(UUID id, String tipo, String contenido, Instant fecha) {

    public static ReporteResponse desde(Reporte reporte) {
        return new ReporteResponse(reporte.getId(), reporte.getTipo().name(), reporte.getContenido(),
                reporte.getFecha());
    }
}
