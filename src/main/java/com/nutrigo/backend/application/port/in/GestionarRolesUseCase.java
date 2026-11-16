package com.nutrigo.backend.application.port.in;

import com.nutrigo.backend.domain.model.Rol;
import com.nutrigo.backend.domain.model.UsuarioIdentidad;

import java.util.List;
import java.util.UUID;

/** Administración de roles elevados (solo ADMIN). */
public interface GestionarRolesUseCase {

    List<UsuarioIdentidad> listarCuentas();

    void asignarRol(UUID adminKeycloakUserId, UUID keycloakUserId, Rol rol);

    void quitarRol(UUID adminKeycloakUserId, UUID keycloakUserId, Rol rol);
}
