package com.nutrigo.backend.infrastructure.adapter.out.persistence;

import com.nutrigo.backend.application.port.out.ReporteRepositoryPort;
import com.nutrigo.backend.domain.model.Reporte;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ReporteRepositoryAdapter implements ReporteRepositoryPort {

    private final ReporteJpaRepository reporteJpaRepository;

    public ReporteRepositoryAdapter(ReporteJpaRepository reporteJpaRepository) {
        this.reporteJpaRepository = reporteJpaRepository;
    }

    @Override
    public Reporte guardar(Reporte reporte) {
        return reporteJpaRepository.save(reporte);
    }

    @Override
    public List<Reporte> buscarPorUsuarioOrdenadoDesc(UUID usuarioId) {
        return reporteJpaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }
}
