package com.nutrigo.backend.application.port.out;

import com.nutrigo.backend.domain.model.Rol;
import com.nutrigo.backend.domain.model.UsuarioIdentidad;

import java.util.List;
import java.util.UUID;

/** Acceso a cuentas y roles del proveedor de identidad (Keycloak Admin API). */
public interface ProveedorIdentidadPort {

    List<UsuarioIdentidad> listarCuentas();

    void asignarRol(UUID keycloakUserId, Rol rol);

    void quitarRol(UUID keycloakUserId, Rol rol);
}
