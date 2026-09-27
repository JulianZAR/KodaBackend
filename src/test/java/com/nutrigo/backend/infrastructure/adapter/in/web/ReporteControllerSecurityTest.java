package com.nutrigo.backend.infrastructure.adapter.in.web;

import com.nutrigo.backend.application.port.in.ConsultarReportesUseCase;
import com.nutrigo.backend.application.port.in.GenerarReporteUseCase;
import com.nutrigo.backend.application.port.in.ObtenerPerfilUseCase;
import com.nutrigo.backend.domain.model.Usuario;
import com.nutrigo.backend.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica las reglas de autorización de /api/reports, no la lógica de
 * negocio (ya cubierta por ReporteApplicationServiceTest).
 */
@WebMvcTest(ReporteController.class)
@Import(SecurityConfig.class)
class ReporteControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private ObtenerPerfilUseCase obtenerPerfilUseCase;

    @MockitoBean
    private GenerarReporteUseCase generarReporteUseCase;

    @MockitoBean
    private ConsultarReportesUseCase consultarReportesUseCase;

    @Test
    void rechazaSinTokenConNoAutorizado() throws Exception {
        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unUsuarioRegularNoPuedeConsultarReportesDeOtro() throws Exception {
        UUID usuarioId = UUID.randomUUID();

        mockMvc.perform(get("/api/reports/" + usuarioId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void unAdminSiPuedeConsultarReportesDeOtroUsuario() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        when(consultarReportesUseCase.consultarReportes(usuarioId)).thenReturn(List.of());

        mockMvc.perform(get("/api/reports/" + usuarioId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void unUsuarioRegularSiPuedeConsultarSusPropiosReportes() throws Exception {
        when(obtenerPerfilUseCase.obtenerOCrearPerfil(any(), any(), any()))
                .thenReturn(new Usuario(UUID.randomUUID(), "ana@nutrigo.local", "Ana"));
        when(consultarReportesUseCase.consultarReportes(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/reports")
                        .with(jwt()
                                .jwt(builder -> builder.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());
    }
}
