package com.nutrigo.backend.application.port.in;

import com.nutrigo.backend.domain.model.ResultadoImc;

import java.util.UUID;

public interface ConsultarImcActualUseCase {

    ResultadoImc consultarImcActual(UUID keycloakUserId);
}
