package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.in.ActualizarPerfilUseCase;
import com.nutrigo.backend.application.port.in.ObtenerPerfilUseCase;
import com.nutrigo.backend.application.port.out.UsuarioRepositoryPort;
import com.nutrigo.backend.domain.model.Meta;
import com.nutrigo.backend.domain.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class PerfilApplicationService implements ObtenerPerfilUseCase, ActualizarPerfilUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public PerfilApplicationService(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public Usuario obtenerOCrearPerfil(UUID keycloakUserId, String email, String nombre) {
        return usuarioRepositoryPort.buscarPorKeycloakUserId(keycloakUserId)
                .map(usuario -> sincronizarYGuardar(usuario, email, nombre))
                .orElseGet(() -> usuarioRepositoryPort.guardar(new Usuario(keycloakUserId, email, nombre)));
    }

    @Override
    public Usuario actualizarPerfil(UUID keycloakUserId, String email, String nombre,
                                     BigDecimal estaturaCm, BigDecimal pesoKg, Meta meta) {
        Usuario usuario = obtenerOCrearPerfil(keycloakUserId, email, nombre);
        usuario.actualizarDatosFisicos(estaturaCm, pesoKg, meta);
        return usuarioRepositoryPort.guardar(usuario);
    }

    private Usuario sincronizarYGuardar(Usuario usuario, String email, String nombre) {
        usuario.sincronizarIdentidad(email, nombre);
        return usuarioRepositoryPort.guardar(usuario);
    }
}
