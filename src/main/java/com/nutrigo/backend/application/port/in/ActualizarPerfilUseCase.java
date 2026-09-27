package com.nutrigo.backend.application.port.in;

import com.nutrigo.backend.domain.model.Meta;
import com.nutrigo.backend.domain.model.Usuario;

import java.math.BigDecimal;
import java.util.UUID;

public interface ActualizarPerfilUseCase {

    Usuario actualizarPerfil(UUID keycloakUserId, String email, String nombre,
                              BigDecimal estaturaCm, BigDecimal pesoKg, Meta meta);
}
