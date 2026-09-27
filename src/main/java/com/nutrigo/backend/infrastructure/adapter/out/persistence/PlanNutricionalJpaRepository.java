package com.nutrigo.backend.infrastructure.adapter.out.persistence;

import com.nutrigo.backend.domain.model.PlanNutricional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlanNutricionalJpaRepository extends JpaRepository<PlanNutricional, UUID> {

    Optional<PlanNutricional> findByUsuarioId(UUID usuarioId);
}
