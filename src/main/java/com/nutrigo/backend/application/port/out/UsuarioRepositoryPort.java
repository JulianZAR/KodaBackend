package com.nutrigo.backend.application.port.out;

import com.nutrigo.backend.domain.model.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositoryPort {

    Optional<Usuario> buscarPorKeycloakUserId(UUID keycloakUserId);

    Usuario guardar(Usuario usuario);
}
