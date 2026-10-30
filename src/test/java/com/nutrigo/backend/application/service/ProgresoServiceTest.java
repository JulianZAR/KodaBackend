package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.in.ObtenerPerfilUseCase;
import com.nutrigo.backend.application.port.out.RegistroProgresoRepositoryPort;
import com.nutrigo.backend.domain.exception.DatosInvalidosException;
import com.nutrigo.backend.domain.exception.PerfilIncompletoException;
import com.nutrigo.backend.domain.model.RegistroProgreso;
import com.nutrigo.backend.domain.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgresoServiceTest {

    @Mock
    private ObtenerPerfilUseCase obtenerPerfilUseCase;

    @Mock
    private RegistroProgresoRepositoryPort registroProgresoRepositoryPort;

    @InjectMocks
    private ProgresoApplicationService progresoService;

    @Test
    @DisplayName("Debe registrar un nuevo pesaje, calcular el IMC correctamente y persistir el registro")
    void debeRegistrarPuntoDeProgresoYCalcularImcCorrectamente() {
        // Arrange
        UUID keycloakUserId = UUID.randomUUID();
        Usuario usuario = new Usuario(keycloakUserId, "carlos@nutrigo.com", "Carlos Perez");
        usuario.actualizarEstaturaYMeta(BigDecimal.valueOf(175), null); // 1.75m

        BigDecimal pesoNuevo = BigDecimal.valueOf(70.0);

        when(obtenerPerfilUseCase.obtenerOCrearPerfil(keycloakUserId, "carlos@nutrigo.com", "Carlos Perez"))
                .thenReturn(usuario);
        when(registroProgresoRepositoryPort.guardar(any(RegistroProgreso.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RegistroProgreso resultado = progresoService.registrarProgreso(
                keycloakUserId, "carlos@nutrigo.com", "Carlos Perez", pesoNuevo);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPesoKg()).isEqualByComparingTo("70.0");
        // IMC = 70 / (1.75 * 1.75) = 22.86
        assertThat(resultado.getImc()).isEqualByComparingTo("22.86");
        assertThat(usuario.getPesoKg()).isEqualByComparingTo("70.0");

        verify(obtenerPerfilUseCase, times(1)).obtenerOCrearPerfil(keycloakUserId, "carlos@nutrigo.com", "Carlos Perez");
        verify(registroProgresoRepositoryPort, times(1)).guardar(any(RegistroProgreso.class));
    }

    @Test
    @DisplayName("Debe lanzar PerfilIncompletoException si el usuario no tiene estatura registrada al pesar")
    void debeFallarSiUsuarioNoTieneEstaturaRegistrada() {
        // Arrange
        UUID keycloakUserId = UUID.randomUUID();
        Usuario usuarioSinEstatura = new Usuario(keycloakUserId, "ana@nutrigo.com", "Ana Gomez");

        when(obtenerPerfilUseCase.obtenerOCrearPerfil(keycloakUserId, "ana@nutrigo.com", "Ana Gomez"))
                .thenReturn(usuarioSinEstatura);

        // Act & Assert
        assertThatThrownBy(() -> progresoService.registrarProgreso(
                keycloakUserId, "ana@nutrigo.com", "Ana Gomez", BigDecimal.valueOf(60.0)))
                .isInstanceOf(PerfilIncompletoException.class)
                .hasMessageContaining("estatura");

        verify(registroProgresoRepositoryPort, never()).guardar(any(RegistroProgreso.class));
    }

    @Test
    @DisplayName("Debe lanzar DatosInvalidosException si el peso es nulo, cero o negativo")
    void debeFallarSiPesoEsInvalido() {
        UUID keycloakUserId = UUID.randomUUID();
        Usuario usuario = new Usuario(keycloakUserId, "test@nutrigo.com", "Test");
        usuario.actualizarEstaturaYMeta(BigDecimal.valueOf(170), null);

        when(obtenerPerfilUseCase.obtenerOCrearPerfil(any(), any(), any())).thenReturn(usuario);

        assertThatThrownBy(() -> progresoService.registrarProgreso(
                keycloakUserId, "test@nutrigo.com", "Test", null))
                .isInstanceOf(DatosInvalidosException.class);

        assertThatThrownBy(() -> progresoService.registrarProgreso(
                keycloakUserId, "test@nutrigo.com", "Test", BigDecimal.ZERO))
                .isInstanceOf(DatosInvalidosException.class);

        assertThatThrownBy(() -> progresoService.registrarProgreso(
                keycloakUserId, "test@nutrigo.com", "Test", BigDecimal.valueOf(-15.5)))
                .isInstanceOf(DatosInvalidosException.class);
    }

    @Test
    @DisplayName("Debe consultar el historial de pesajes ordenado cronológicamente descendente")
    void debeConsultarHistorialOrdenado() {
        // Arrange
        UUID keycloakUserId = UUID.randomUUID();
        Usuario usuario = new Usuario(keycloakUserId, "laura@nutrigo.com", "Laura");
        usuario.actualizarEstaturaYMeta(BigDecimal.valueOf(165), null);

        RegistroProgreso reg1 = new RegistroProgreso(usuario, BigDecimal.valueOf(58.0), BigDecimal.valueOf(21.30));
        RegistroProgreso reg2 = new RegistroProgreso(usuario, BigDecimal.valueOf(59.5), BigDecimal.valueOf(21.85));

        when(obtenerPerfilUseCase.obtenerOCrearPerfil(keycloakUserId, "laura@nutrigo.com", "Laura"))
                .thenReturn(usuario);
        when(registroProgresoRepositoryPort.buscarPorUsuarioOrdenadoDesc(usuario.getId()))
                .thenReturn(List.of(reg1, reg2));

        // Act
        List<RegistroProgreso> historial = progresoService.consultarHistorial(keycloakUserId, "laura@nutrigo.com", "Laura");

        // Assert
        assertThat(historial).hasSize(2).containsExactly(reg1, reg2);
        verify(registroProgresoRepositoryPort).buscarPorUsuarioOrdenadoDesc(usuario.getId());
    }
}
