package com.nutrigo.backend.application.service;

import java.util.Optional;
import org.mockito.ArgumentCaptor;
import com.nutrigo.backend.domain.model.Usuario;
import com.nutrigo.backend.domain.model.TipoNotificacion;
import com.nutrigo.backend.domain.model.Notificacion;
import com.nutrigo.backend.application.port.out.UsuarioRepositoryPort;
import com.nutrigo.backend.application.port.out.NotificacionRepositoryPort;
import com.nutrigo.backend.application.port.out.ProveedorIdentidadPort;
import com.nutrigo.backend.domain.exception.OperacionNoPermitidaException;
import com.nutrigo.backend.domain.model.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RolesApplicationServiceTest {

    @Mock
    private ProveedorIdentidadPort proveedorIdentidadPort;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private NotificacionRepositoryPort notificacionRepositoryPort;

    private RolesApplicationService service;

    private final UUID admin = UUID.randomUUID();
    private final UUID otro = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new RolesApplicationService(proveedorIdentidadPort, usuarioRepositoryPort,
                notificacionRepositoryPort);
    }

    @Test
    void unAdminPuedeNombrarNutricionista() {
        service.asignarRol(admin, otro, Rol.NUTRITIONIST);

        verify(proveedorIdentidadPort).asignarRol(otro, Rol.NUTRITIONIST);
    }

    @Test
    void unAdminPuedeQuitarElRolAdminAOtraCuenta() {
        service.quitarRol(admin, otro, Rol.ADMIN);

        verify(proveedorIdentidadPort).quitarRol(otro, Rol.ADMIN);
    }

    @Test
    void unAdminNoPuedeQuitarseSuPropioRolAdmin() {
        assertThatThrownBy(() -> service.quitarRol(admin, admin, Rol.ADMIN))
                .isInstanceOf(OperacionNoPermitidaException.class);

        verify(proveedorIdentidadPort, never()).quitarRol(any(), any());
    }

    @Test
    void elRolBaseUserNoSePuedeAsignarNiQuitar() {
        assertThatThrownBy(() -> service.asignarRol(admin, otro, Rol.USER))
                .isInstanceOf(OperacionNoPermitidaException.class);
        assertThatThrownBy(() -> service.quitarRol(admin, otro, Rol.USER))
                .isInstanceOf(OperacionNoPermitidaException.class);

        verify(proveedorIdentidadPort, never()).asignarRol(any(), any());
        verify(proveedorIdentidadPort, never()).quitarRol(any(), any());
    }

    @Test
    void notificaAlUsuarioCuandoCambiaSuRolSiYaTienePerfil() {
        Usuario usuario = new Usuario(otro, "marta@nutrigo.local", "Marta");
        when(usuarioRepositoryPort.buscarPorKeycloakUserId(otro)).thenReturn(Optional.of(usuario));
        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);

        service.asignarRol(admin, otro, Rol.NUTRITIONIST);
        service.quitarRol(admin, otro, Rol.NUTRITIONIST);

        verify(notificacionRepositoryPort, times(2)).guardar(captor.capture());
        assertThat(captor.getAllValues()).extracting(Notificacion::getTipo)
                .containsExactly(TipoNotificacion.ROL_ASIGNADO, TipoNotificacion.ROL_RETIRADO);
        assertThat(captor.getAllValues().get(0).getTitulo()).isEqualTo("Ahora eres nutricionista");
    }

    @Test
    void siLaCuentaAunNoTienePerfilNoSeNotificaPeroElRolSiCambia() {
        when(usuarioRepositoryPort.buscarPorKeycloakUserId(otro)).thenReturn(Optional.empty());

        service.asignarRol(admin, otro, Rol.ADMIN);

        verify(proveedorIdentidadPort).asignarRol(otro, Rol.ADMIN);
        verify(notificacionRepositoryPort, never()).guardar(any());
    }
}
