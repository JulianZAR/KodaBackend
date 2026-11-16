package com.nutrigo.backend.infrastructure.adapter.in.web;

import com.nutrigo.backend.application.port.in.ActualizarPerfilUseCase;
import com.nutrigo.backend.application.port.in.ConsultarPacientesUseCase;
import com.nutrigo.backend.application.port.in.ConsultarProgresoUseCase;
import com.nutrigo.backend.application.port.in.ConsultarReportesUseCase;
import com.nutrigo.backend.application.port.in.GenerarReporteUseCase;
import com.nutrigo.backend.application.port.in.GestionarRolesUseCase;
import com.nutrigo.backend.application.port.in.ObtenerPerfilUseCase;
import com.nutrigo.backend.application.port.in.RegistrarProgresoUseCase;
import com.nutrigo.backend.domain.exception.OperacionNoPermitidaException;
import com.nutrigo.backend.domain.model.Rol;
import com.nutrigo.backend.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Autorización de las rutas por rol: pacientes (NUTRITIONIST/ADMIN) y
 * administración de roles (solo ADMIN).
 */
@WebMvcTest({UsuarioController.class, ProgresoController.class, ReporteController.class, AdminController.class,
        NotificacionController.class})
@Import(SecurityConfig.class)
class RolesYPacientesSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private JwtDecoder jwtDecoder;
    @MockitoBean private ObtenerPerfilUseCase obtenerPerfilUseCase;
    @MockitoBean private ActualizarPerfilUseCase actualizarPerfilUseCase;
    @MockitoBean private ConsultarPacientesUseCase consultarPacientesUseCase;
    @MockitoBean private RegistrarProgresoUseCase registrarProgresoUseCase;
    @MockitoBean private ConsultarProgresoUseCase consultarProgresoUseCase;
    @MockitoBean private GenerarReporteUseCase generarReporteUseCase;
    @MockitoBean private ConsultarReportesUseCase consultarReportesUseCase;
    @MockitoBean private GestionarRolesUseCase gestionarRolesUseCase;
    @MockitoBean private com.nutrigo.backend.application.port.in.GestionarNotificacionesUseCase gestionarNotificacionesUseCase;

    private final UUID paciente = UUID.randomUUID();

    private static RequestPostProcessor con(String rol) {
        return jwt().jwt(j -> j.subject(UUID.randomUUID().toString()))
                .authorities(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Test
    void unUsuarioRegularNoVePacientes() throws Exception {
        mockMvc.perform(get("/api/users").with(con("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/users/" + paciente).with(con("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/progress/" + paciente).with(con("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/reports/" + paciente).with(con("USER"))).andExpect(status().isForbidden());
    }

    @Test
    void unNutricionistaVeLaListaDePacientesYSuHistorial() throws Exception {
        when(consultarPacientesUseCase.listarPacientes()).thenReturn(List.of());
        when(consultarPacientesUseCase.consultarHistorialDePaciente(paciente)).thenReturn(List.of());

        mockMvc.perform(get("/api/users").with(con("NUTRITIONIST"))).andExpect(status().isOk());
        mockMvc.perform(get("/api/progress/" + paciente).with(con("NUTRITIONIST"))).andExpect(status().isOk());
    }

    @Test
    void soloElAdminGestionaRoles() throws Exception {
        UUID cuenta = UUID.randomUUID();
        mockMvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/admin/users").with(con("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/users").with(con("NUTRITIONIST"))).andExpect(status().isForbidden());
        mockMvc.perform(put("/api/admin/users/" + cuenta + "/roles/NUTRITIONIST").with(con("NUTRITIONIST")))
                .andExpect(status().isForbidden());

        when(gestionarRolesUseCase.listarCuentas()).thenReturn(List.of());
        mockMvc.perform(get("/api/admin/users").with(con("ADMIN"))).andExpect(status().isOk());
        mockMvc.perform(put("/api/admin/users/" + cuenta + "/roles/NUTRITIONIST").with(con("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void reglasDeNegocioYRolesInvalidosDevuelvenErroresClaros() throws Exception {
        UUID cuenta = UUID.randomUUID();
        doThrow(new OperacionNoPermitidaException("No puedes quitarte a ti mismo el rol de administrador."))
                .when(gestionarRolesUseCase).quitarRol(org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.eq(cuenta), org.mockito.ArgumentMatchers.eq(Rol.ADMIN));

        mockMvc.perform(delete("/api/admin/users/" + cuenta + "/roles/ADMIN").with(con("ADMIN")))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/api/admin/users/" + cuenta + "/roles/SUPERUSUARIO").with(con("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void lasNotificacionesRequierenSesionPeroCualquierRolLasVe() throws Exception {
        mockMvc.perform(get("/api/notifications")).andExpect(status().isUnauthorized());

        com.nutrigo.backend.domain.model.Usuario usuario =
                new com.nutrigo.backend.domain.model.Usuario(UUID.randomUUID(), "ana@nutrigo.local", "Ana");
        when(obtenerPerfilUseCase.obtenerOCrearPerfil(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(usuario);
        when(gestionarNotificacionesUseCase.listar(org.mockito.ArgumentMatchers.any())).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications").with(con("USER"))).andExpect(status().isOk());
        mockMvc.perform(post("/api/notifications/read-all").with(con("USER"))).andExpect(status().isNoContent());
    }
}
