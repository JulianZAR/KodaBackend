package com.nutrigo.backend.application.port.out;

import java.util.List;
import com.nutrigo.backend.domain.model.PlanNutricional;

import java.util.Optional;
import java.util.UUID;

public interface PlanNutricionalRepositoryPort {

    Optional<PlanNutricional> buscarPorUsuarioId(UUID usuarioId);

    PlanNutricional guardar(PlanNutricional plan);

    List<PlanNutricional> listarTodos();
}
