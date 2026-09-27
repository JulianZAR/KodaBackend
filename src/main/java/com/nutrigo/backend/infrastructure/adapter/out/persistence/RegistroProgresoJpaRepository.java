package com.nutrigo.backend.infrastructure.adapter.out.persistence;

import com.nutrigo.backend.domain.model.RegistroProgreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RegistroProgresoJpaRepository extends JpaRepository<RegistroProgreso, UUID> {

    List<RegistroProgreso> findByUsuarioIdOrderByFechaDesc(UUID usuarioId);
}
