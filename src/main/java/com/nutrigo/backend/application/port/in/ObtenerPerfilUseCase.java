package com.nutrigo.backend.application.port.in;

import com.nutrigo.backend.domain.model.Usuario;

import java.util.UUID;

public interface ObtenerPerfilUseCase {

    Usuario obtenerOCrearPerfil(UUID keycloakUserId, String email, String nombre);
}
