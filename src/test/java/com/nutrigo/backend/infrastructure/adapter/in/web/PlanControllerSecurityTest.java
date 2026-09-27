package com.nutrigo.backend.infrastructure.adapter.in.web;

import com.nutrigo.backend.application.port.in.AsignarPlanUseCase;
import com.nutrigo.backend.application.port.in.ConsultarPlanUseCase;
import com.nutrigo.backend.application.port.in.ObtenerPerfilUseCase;
import com.nutrigo.backend.domain.model.Meta;
import com.nutrigo.backend.domain.model.PlanNutricional;
import com.nutrigo.backend.domain.model.Usuario;
import com.nutrigo.backend.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica las reglas de autorización de /api/plans, no la lógica de negocio
 * (ya cubierta por PlanApplicationServiceTest). El JwtDecoder se mockea para
 * que este test no dependa de un Keycloak real ni de red durante el build.
 */
@WebMvcTest(PlanController.class)
@Import(SecurityConfig.class)
class PlanControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private ObtenerPerfilUseCase obtenerPerfilUseCase;

    @MockitoBean
    private AsignarPlanUseCase asignarPlanUseCase;

    @MockitoBean
    private ConsultarPlanUseCase consultarPlanUseCase;

    @Test
    void rechazaSinTokenConNoAutorizado() throws Exception {
        mockMvc.perform(get("/api/plans/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unUsuarioRegularNoPuedeAsignarPlanes() throws Exception {
        UUID usuarioId = UUID.randomUUID();

        mockMvc.perform(post("/api/plans/" + usuarioId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Plan\",\"objetivo\":\"MANTENER\",\"descripcion\":\"x\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unUsuarioRegularNoPuedeConsultarElPlanDeOtro() throws Exception {
        UUID usuarioId = UUID.randomUUID();

        mockMvc.perform(get("/api/plans/" + usuarioId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void unNutricionistaSiPuedeAsignarPlanes() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario(UUID.randomUUID(), "ana@nutrigo.local", "Ana");
        when(asignarPlanUseCase.asignarPlan(eq(usuarioId), any(), any(), any()))
                .thenReturn(new PlanNutricional(usuario, "Plan", Meta.MANTENER, "x"));

        mockMvc.perform(post("/api/plans/" + usuarioId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_NUTRITIONIST")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Plan\",\"objetivo\":\"MANTENER\",\"descripcion\":\"x\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void unAdminSiPuedeConsultarElPlanDeOtroUsuario() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario(UUID.randomUUID(), "ana@nutrigo.local", "Ana");
        when(consultarPlanUseCase.consultarPlan(usuarioId))
                .thenReturn(java.util.Optional.of(new PlanNutricional(usuario, "Plan", Meta.MANTENER, "x")));

        mockMvc.perform(get("/api/plans/" + usuarioId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }
}
