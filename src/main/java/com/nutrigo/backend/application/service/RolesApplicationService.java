package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.in.GestionarRolesUseCase;
import com.nutrigo.backend.application.port.out.NotificacionRepositoryPort;
import com.nutrigo.backend.application.port.out.ProveedorIdentidadPort;
import com.nutrigo.backend.application.port.out.UsuarioRepositoryPort;
import com.nutrigo.backend.domain.exception.OperacionNoPermitidaException;
import com.nutrigo.backend.domain.model.Notificacion;
import com.nutrigo.backend.domain.model.Rol;
import com.nutrigo.backend.domain.model.UsuarioIdentidad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Reglas para administrar roles elevados. Los roles viven solo en Keycloak;
 * aquí se validan las reglas y se delega el cambio al proveedor de identidad.
 */
@Service
public class RolesApplicationService implements GestionarRolesUseCase {

    private static final Logger log = LoggerFactory.getLogger(RolesApplicationService.class);

    private final ProveedorIdentidadPort proveedorIdentidadPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final NotificacionRepositoryPort notificacionRepositoryPort;

    public RolesApplicationService(ProveedorIdentidadPort proveedorIdentidadPort,
                                   UsuarioRepositoryPort usuarioRepositoryPort,
                                   NotificacionRepositoryPort notificacionRepositoryPort) {
        this.proveedorIdentidadPort = proveedorIdentidadPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.notificacionRepositoryPort = notificacionRepositoryPort;
    }

    @Override
    public List<UsuarioIdentidad> listarCuentas() {
        return proveedorIdentidadPort.listarCuentas();
    }

    @Override
    public void asignarRol(UUID adminKeycloakUserId, UUID keycloakUserId, Rol rol) {
        validarAsignable(rol);
        proveedorIdentidadPort.asignarRol(keycloakUserId, rol);
        usuarioRepositoryPort.buscarPorKeycloakUserId(keycloakUserId)
                .ifPresent(u -> notificacionRepositoryPort.guardar(Notificacion.rolAsignado(u, rol)));
        log.info("Rol {} asignado a {} por el administrador {}", rol, keycloakUserId, adminKeycloakUserId);
    }

    @Override
    public void quitarRol(UUID adminKeycloakUserId, UUID keycloakUserId, Rol rol) {
        validarAsignable(rol);
        if (rol == Rol.ADMIN && keycloakUserId.equals(adminKeycloakUserId)) {
            throw new OperacionNoPermitidaException(
                    "No puedes quitarte a ti mismo el rol de administrador.");
        }
        proveedorIdentidadPort.quitarRol(keycloakUserId, rol);
        usuarioRepositoryPort.buscarPorKeycloakUserId(keycloakUserId)
                .ifPresent(u -> notificacionRepositoryPort.guardar(Notificacion.rolRetirado(u, rol)));
        log.info("Rol {} retirado a {} por el administrador {}", rol, keycloakUserId, adminKeycloakUserId);
    }

    private void validarAsignable(Rol rol) {
        if (!rol.esAsignable()) {
            throw new OperacionNoPermitidaException(
                    "El rol " + rol + " es el rol base de todas las cuentas y no se puede modificar.");
        }
    }
}
