package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.in.AsignarPlanUseCase;
import com.nutrigo.backend.application.port.in.ConsultarPlanUseCase;
import com.nutrigo.backend.application.port.out.PlanNutricionalRepositoryPort;
import com.nutrigo.backend.application.port.out.UsuarioRepositoryPort;
import com.nutrigo.backend.domain.exception.UsuarioNoEncontradoException;
import com.nutrigo.backend.domain.model.Meta;
import com.nutrigo.backend.domain.model.PlanNutricional;
import com.nutrigo.backend.domain.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PlanApplicationService implements AsignarPlanUseCase, ConsultarPlanUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PlanNutricionalRepositoryPort planNutricionalRepositoryPort;

    public PlanApplicationService(UsuarioRepositoryPort usuarioRepositoryPort,
                                   PlanNutricionalRepositoryPort planNutricionalRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.planNutricionalRepositoryPort = planNutricionalRepositoryPort;
    }

    @Override
    public PlanNutricional asignarPlan(UUID usuarioId, String nombre, Meta objetivo, String descripcion) {
        Usuario usuario = usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("No existe un usuario con id " + usuarioId));

        PlanNutricional plan = planNutricionalRepositoryPort.buscarPorUsuarioId(usuarioId)
                .map(existente -> {
                    existente.actualizar(nombre, objetivo, descripcion);
                    return existente;
                })
                .orElseGet(() -> new PlanNutricional(usuario, nombre, objetivo, descripcion));

        return planNutricionalRepositoryPort.guardar(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlanNutricional> consultarPlan(UUID usuarioId) {
        return planNutricionalRepositoryPort.buscarPorUsuarioId(usuarioId);
    }
}
