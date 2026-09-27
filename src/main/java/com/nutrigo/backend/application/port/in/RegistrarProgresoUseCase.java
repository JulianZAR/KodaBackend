package com.nutrigo.backend.application.port.in;

import com.nutrigo.backend.domain.model.RegistroProgreso;

import java.math.BigDecimal;
import java.util.UUID;

public interface RegistrarProgresoUseCase {

    RegistroProgreso registrarProgreso(UUID keycloakUserId, String email, String nombre, BigDecimal pesoKg);
}
