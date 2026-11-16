package com.nutrigo.backend.infrastructure.adapter.out.persistence;

import java.util.List;
import com.nutrigo.backend.application.port.out.UsuarioRepositoryPort;
import com.nutrigo.backend.domain.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Optional<Usuario> buscarPorKeycloakUserId(UUID keycloakUserId) {
        return usuarioJpaRepository.findByKeycloakUserId(keycloakUserId);
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarioJpaRepository.findById(id);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return usuarioJpaRepository.save(usuario);
    }

    @Override
    public void crearSiNoExiste(Usuario usuario) {
        usuarioJpaRepository.insertarSiNoExiste(UUID.randomUUID(), usuario.getKeycloakUserId(),
                usuario.getNombre(), usuario.getEmail(), usuario.getFechaRegistro());
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioJpaRepository.findAllByOrderByNombreAsc();
    }
}
