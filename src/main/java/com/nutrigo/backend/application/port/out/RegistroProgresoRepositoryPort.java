package com.nutrigo.backend.application.port.out;

import com.nutrigo.backend.domain.model.RegistroProgreso;

import java.util.List;
import java.util.UUID;

public interface RegistroProgresoRepositoryPort {

    RegistroProgreso guardar(RegistroProgreso registro);

    List<RegistroProgreso> buscarPorUsuarioOrdenadoDesc(UUID usuarioId);
}
