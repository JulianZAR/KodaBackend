package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.in.ObtenerPerfilUseCase;
import com.nutrigo.backend.application.port.out.RegistroProgresoRepositoryPort;
import com.nutrigo.backend.domain.exception.PerfilIncompletoException;
import com.nutrigo.backend.domain.model.RegistroProgreso;
import com.nutrigo.backend.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgresoApplicationServiceTest {

    @Mock
    private ObtenerPerfilUseCase obtenerPerfilUseCase;

    @Mock
    private RegistroProgresoRepositoryPort registroProgresoRepositoryPort;

    private ProgresoApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ProgresoApplicationService(obtenerPerfilUseCase, registroProgresoRepositoryPort);
    }

    @Test
    void registraUnPuntoDeProgresoYActualizaElPesoDelPerfil() {
        UUID keycloakUserId = UUID.randomUUID();
        Usuario usuario = new Usuario(keycloakUserId, "ana@nutrigo.local", "Ana");
        usuario.actualizarEstaturaYMeta(BigDecimal.valueOf(170), null);
        when(obtenerPerfilUseCase.obtenerOCrearPerfil(keycloakUserId, "ana@nutrigo.local", "Ana")).thenReturn(usuario);
        when(registroProgresoRepositoryPort.guardar(any(RegistroProgreso.class))).thenAnswer(inv -> inv.getArgument(0));

        RegistroProgreso registro = service.registrarProgreso(keycloakUserId, "ana@nutrigo.local", "Ana",
                BigDecimal.valueOf(65));

        assertThat(registro.getPesoKg()).isEqualByComparingTo("65");
        assertThat(registro.getImc()).isEqualByComparingTo("22.49");
        assertThat(usuario.getPesoKg()).isEqualByComparingTo("65");
    }

    @Test
    void fallaSiElUsuarioNoTieneEstaturaRegistrada() {
        UUID keycloakUserId = UUID.randomUUID();
        Usuario usuario = new Usuario(keycloakUserId, "ana@nutrigo.local", "Ana");
        when(obtenerPerfilUseCase.obtenerOCrearPerfil(keycloakUserId, "ana@nutrigo.local", "Ana")).thenReturn(usuario);

        assertThatThrownBy(() -> service.registrarProgreso(keycloakUserId, "ana@nutrigo.local", "Ana",
                BigDecimal.valueOf(65)))
                .isInstanceOf(PerfilIncompletoException.class);
    }

    @Test
    void consultaElHistorialOrdenadoDelUsuario() {
        UUID keycloakUserId = UUID.randomUUID();
        Usuario usuario = new Usuario(keycloakUserId, "ana@nutrigo.local", "Ana");
        when(obtenerPerfilUseCase.obtenerOCrearPerfil(keycloakUserId, "ana@nutrigo.local", "Ana")).thenReturn(usuario);
        RegistroProgreso registro = new RegistroProgreso(usuario, BigDecimal.valueOf(65), BigDecimal.valueOf(22.49));
        when(registroProgresoRepositoryPort.buscarPorUsuarioOrdenadoDesc(usuario.getId()))
                .thenReturn(List.of(registro));

        List<RegistroProgreso> historial = service.consultarHistorial(keycloakUserId, "ana@nutrigo.local", "Ana");

        assertThat(historial).containsExactly(registro);
    }
}
