package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.out.UsuarioRepositoryPort;
import com.nutrigo.backend.domain.exception.PerfilIncompletoException;
import com.nutrigo.backend.domain.model.ResultadoImc;
import com.nutrigo.backend.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImcApplicationServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    private ImcApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ImcApplicationService(usuarioRepositoryPort);
    }

    @Test
    void calculaDeFormaStatelessSinNecesidadDePerfil() {
        ResultadoImc resultado = service.calcular(BigDecimal.valueOf(70), BigDecimal.valueOf(175));

        assertThat(resultado.valor()).isEqualByComparingTo("22.86");
    }

    @Test
    void consultaElImcActualUsandoElPerfilGuardado() {
        UUID keycloakUserId = UUID.randomUUID();
        Usuario usuario = new Usuario(keycloakUserId, "ana@nutrigo.local", "Ana");
        usuario.actualizarDatosFisicos(BigDecimal.valueOf(160), BigDecimal.valueOf(55), null);
        when(usuarioRepositoryPort.buscarPorKeycloakUserId(keycloakUserId)).thenReturn(Optional.of(usuario));

        ResultadoImc resultado = service.consultarImcActual(keycloakUserId);

        assertThat(resultado.valor()).isEqualByComparingTo("21.48");
    }

    @Test
    void fallaSiElPerfilNoTienePesoNiEstatura() {
        UUID keycloakUserId = UUID.randomUUID();
        Usuario usuario = new Usuario(keycloakUserId, "ana@nutrigo.local", "Ana");
        when(usuarioRepositoryPort.buscarPorKeycloakUserId(keycloakUserId)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> service.consultarImcActual(keycloakUserId))
                .isInstanceOf(PerfilIncompletoException.class);
    }

    @Test
    void fallaSiElPerfilNoExiste() {
        UUID keycloakUserId = UUID.randomUUID();
        when(usuarioRepositoryPort.buscarPorKeycloakUserId(keycloakUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultarImcActual(keycloakUserId))
                .isInstanceOf(PerfilIncompletoException.class);
    }
}
