package com.nutrigo.backend.application.port.out;

import com.nutrigo.backend.domain.model.Reporte;

import java.util.List;
import java.util.UUID;

public interface ReporteRepositoryPort {

    Reporte guardar(Reporte reporte);

    List<Reporte> buscarPorUsuarioOrdenadoDesc(UUID usuarioId);
}
