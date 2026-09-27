package com.nutrigo.backend.application.port.in;

import com.nutrigo.backend.domain.model.RegistroProgreso;

import java.util.List;
import java.util.UUID;

public interface ConsultarProgresoUseCase {

    List<RegistroProgreso> consultarHistorial(UUID keycloakUserId, String email, String nombre);
}
