package com.nutrigo.backend.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private Jwt jwt;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Debe autenticar y extraer la información del usuario desde los claims del token JWT")
    void debeAutenticarYExtraerClaimsCorrectamente() {
        // Arrange
        UUID expectedId = UUID.randomUUID();
        when(jwt.getSubject()).thenReturn(expectedId.toString());
        when(jwt.getClaimAsString("email")).thenReturn("julian@nutrigo.com");
        when(jwt.getClaimAsString("name")).thenReturn("Julian Villamizar");
        when(jwt.getClaimAsMap("realm_access")).thenReturn(Map.of("roles", List.of("USER", "NUTRITIONIST")));

        // Act
        AuthService.UserPrincipal principal = authService.autenticarYExtraerPrincipal(jwt);

        // Assert
        assertThat(principal).isNotNull();
        assertThat(principal.keycloakUserId()).isEqualTo(expectedId);
        assertThat(principal.email()).isEqualTo("julian@nutrigo.com");
        assertThat(principal.name()).isEqualTo("Julian Villamizar");
        assertThat(principal.roles()).containsExactlyInAnyOrder("USER", "NUTRITIONIST");
    }

    @Test
    @DisplayName("Debe utilizar preferred_username si el claim name es nulo")
    void debeUsarPreferredUsernameSiNombreEsNulo() {
        // Arrange
        UUID expectedId = UUID.randomUUID();
        when(jwt.getSubject()).thenReturn(expectedId.toString());
        when(jwt.getClaimAsString("email")).thenReturn("admin@nutrigo.local");
        when(jwt.getClaimAsString("name")).thenReturn(null);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("admin.dev");
        when(jwt.getClaimAsMap("realm_access")).thenReturn(Map.of("roles", List.of("ADMIN")));

        // Act
        AuthService.UserPrincipal principal = authService.autenticarYExtraerPrincipal(jwt);

        // Assert
        assertThat(principal.name()).isEqualTo("admin.dev");
        assertThat(principal.roles()).contains("ADMIN");
    }

    @Test
    @DisplayName("Debe verificar correctamente si el usuario posee un rol determinado")
    void debeVerificarExistenciaDeRol() {
        when(jwt.getClaimAsMap("realm_access")).thenReturn(Map.of("roles", List.of("USER", "NUTRITIONIST")));

        assertThat(authService.tieneRol(jwt, "USER")).isTrue();
        assertThat(authService.tieneRol(jwt, "NUTRITIONIST")).isTrue();
        assertThat(authService.tieneRol(jwt, "ADMIN")).isFalse();
        assertThat(authService.tieneRol(null, "USER")).isFalse();
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el JWT es nulo")
    void debeLanzarExcepcionSiJwtEsNulo() {
        assertThatThrownBy(() -> authService.autenticarYExtraerPrincipal(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede ser nulo");
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el subject del token está ausente o vacío")
    void debeLanzarExcepcionSiSubjectEsInvalido() {
        when(jwt.getSubject()).thenReturn("");

        assertThatThrownBy(() -> authService.autenticarYExtraerPrincipal(jwt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sub");
    }
}

