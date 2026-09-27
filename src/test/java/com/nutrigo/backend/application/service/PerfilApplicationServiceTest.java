package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.out.UsuarioRepositoryPort;
import com.nutrigo.backend.domain.model.Meta;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilApplicationServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    private PerfilApplicationService service;

    @BeforeEach
    void setUp() {
        service = new PerfilApplicationService(usuarioRepositoryPort);
    }

    @Test
    void creaElPerfilLaPrimeraVezQueNoExiste() {
        UUID keycloakUserId = UUID.randomUUID();
        when(usuarioRepositoryPort.buscarPorKeycloakUserId(keycloakUserId)).thenReturn(Optional.empty());
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario usuario = service.obtenerOCrearPerfil(keycloakUserId, "ana@nutrigo.local", "Ana");

        assertThat(usuario.getKeycloakUserId()).isEqualTo(keycloakUserId);
        assertThat(usuario.getEmail()).isEqualTo("ana@nutrigo.local");
        verify(usuarioRepositoryPort).guardar(any(Usuario.class));
    }

    @Test
    void noDuplicaElPerfilSiYaExiste() {
        UUID keycloakUserId = UUID.randomUUID();
        Usuario existente = new Usuario(keycloakUserId, "ana@nutrigo.local", "Ana");
        when(usuarioRepositoryPort.buscarPorKeycloakUserId(keycloakUserId)).thenReturn(Optional.of(existente));
        when(usuarioRepositoryPort.guardar(existente)).thenReturn(existente);

        Usuario usuario = service.obtenerOCrearPerfil(keycloakUserId, "ana@nutrigo.local", "Ana");

        assertThat(usuario).isSameAs(existente);
        verify(usuarioRepositoryPort, times(1)).guardar(existente);
    }

    @Test
    void actualizaEstaturaPesoYMeta() {
        UUID keycloakUserId = UUID.randomUUID();
        Usuario existente = new Usuario(keycloakUserId, "ana@nutrigo.local", "Ana");
        when(usuarioRepositoryPort.buscarPorKeycloakUserId(keycloakUserId)).thenReturn(Optional.of(existente));
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario usuario = service.actualizarPerfil(keycloakUserId, "ana@nutrigo.local", "Ana",
                BigDecimal.valueOf(165), BigDecimal.valueOf(60), Meta.MANTENER);

        assertThat(usuario.getEstaturaCm()).isEqualByComparingTo("165");
        assertThat(usuario.getPesoKg()).isEqualByComparingTo("60");
        assertThat(usuario.getMeta()).isEqualTo(Meta.MANTENER);
    }
}
