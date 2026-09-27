package com.nutrigo.backend.infrastructure.adapter.out.persistence;

import com.nutrigo.backend.domain.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReporteJpaRepository extends JpaRepository<Reporte, UUID> {

    List<Reporte> findByUsuarioIdOrderByFechaDesc(UUID usuarioId);
}
