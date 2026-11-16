package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.out.NotificacionRepositoryPort;
import com.nutrigo.backend.domain.exception.NotificacionNoEncontradaException;
import com.nutrigo.backend.domain.model.Notificacion;
import com.nutrigo.backend.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionesApplicationServiceTest {

    @Mock
    private NotificacionRepositoryPort notificacionRepositoryPort;

    private NotificacionesApplicationService service;

    @BeforeEach
    void setUp() {
        service = new NotificacionesApplicationService(notificacionRepositoryPort);
    }

    @Test
    void marcaComoLeidaUnaNotificacionPropia() {
        UUID usuarioId = UUID.randomUUID();
        UUID notificacionId = UUID.randomUUID();
        Usuario usuario = new Usuario(UUID.randomUUID(), "ana@nutrigo.local", "Ana");
        Notificacion notificacion = Notificacion.reporteGenerado(usuario);
        when(notificacionRepositoryPort.buscarDeUsuario(notificacionId, usuarioId)).thenReturn(Optional.of(notificacion));

        service.marcarLeida(usuarioId, notificacionId);

        assertThat(notificacion.isLeida()).isTrue();
        verify(notificacionRepositoryPort).guardar(notificacion);
    }

    @Test
    void noPermiteMarcarUnaNotificacionAjena() {
        UUID usuarioId = UUID.randomUUID();
        UUID ajena = UUID.randomUUID();
        when(notificacionRepositoryPort.buscarDeUsuario(ajena, usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.marcarLeida(usuarioId, ajena))
                .isInstanceOf(NotificacionNoEncontradaException.class);
        verify(notificacionRepositoryPort, never()).guardar(any());
    }
}
