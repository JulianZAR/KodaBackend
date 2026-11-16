package com.nutrigo.backend.application.port.out;

import java.util.List;
import com.nutrigo.backend.domain.model.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositoryPort {

    Optional<Usuario> buscarPorKeycloakUserId(UUID keycloakUserId);

    Optional<Usuario> buscarPorId(UUID id);

    Usuario guardar(Usuario usuario);

    /**
     * Inserta el usuario solo si aún no existe uno con su keycloakUserId (o su
     * email). Es seguro ante peticiones concurrentes del mismo usuario nuevo.
     */
    void crearSiNoExiste(Usuario usuario);

    List<Usuario> listarTodos();
}
