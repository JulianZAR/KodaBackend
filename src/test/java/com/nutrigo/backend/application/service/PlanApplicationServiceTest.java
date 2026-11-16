package com.nutrigo.backend.application.service;

import org.mockito.ArgumentCaptor;
import com.nutrigo.backend.domain.model.TipoNotificacion;
import com.nutrigo.backend.domain.model.Notificacion;
import com.nutrigo.backend.application.port.out.NotificacionRepositoryPort;
import com.nutrigo.backend.application.port.out.PlanNutricionalRepositoryPort;
import com.nutrigo.backend.application.port.out.UsuarioRepositoryPort;
import com.nutrigo.backend.domain.exception.UsuarioNoEncontradoException;
import com.nutrigo.backend.domain.model.Meta;
import com.nutrigo.backend.domain.model.PlanNutricional;
import com.nutrigo.backend.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanApplicationServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PlanNutricionalRepositoryPort planNutricionalRepositoryPort;

    @Mock
    private NotificacionRepositoryPort notificacionRepositoryPort;

    private PlanApplicationService service;

    @BeforeEach
    void setUp() {
        service = new PlanApplicationService(usuarioRepositoryPort, planNutricionalRepositoryPort,
                notificacionRepositoryPort);
    }

    @Test
    void creaUnPlanNuevoSiElUsuarioAunNoTieneUno() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario(UUID.randomUUID(), "ana@nutrigo.local", "Ana");
        when(usuarioRepositoryPort.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
        when(planNutricionalRepositoryPort.buscarPorUsuarioId(usuarioId)).thenReturn(Optional.empty());
        when(planNutricionalRepositoryPort.guardar(any(PlanNutricional.class))).thenAnswer(inv -> inv.getArgument(0));

        PlanNutricional plan = service.asignarPlan(usuarioId, "Plan fase 1", Meta.PERDER_PESO, "Detalle");

        assertThat(plan.getNombre()).isEqualTo("Plan fase 1");
        assertThat(plan.getObjetivo()).isEqualTo(Meta.PERDER_PESO);
    }

    @Test
    void reemplazaElPlanExistenteEnLugarDeCrearUnoNuevo() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario(UUID.randomUUID(), "ana@nutrigo.local", "Ana");
        PlanNutricional existente = new PlanNutricional(usuario, "Plan viejo", Meta.MANTENER, "Viejo");
        when(usuarioRepositoryPort.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
        when(planNutricionalRepositoryPort.buscarPorUsuarioId(usuarioId)).thenReturn(Optional.of(existente));
        when(planNutricionalRepositoryPort.guardar(existente)).thenReturn(existente);

        PlanNutricional plan = service.asignarPlan(usuarioId, "Plan nuevo", Meta.GANAR_MASA, "Nuevo");

        assertThat(plan).isSameAs(existente);
        assertThat(plan.getNombre()).isEqualTo("Plan nuevo");
        assertThat(plan.getObjetivo()).isEqualTo(Meta.GANAR_MASA);
    }

    @Test
    void fallaSiElUsuarioNoExiste() {
        UUID usuarioId = UUID.randomUUID();
        when(usuarioRepositoryPort.buscarPorId(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.asignarPlan(usuarioId, "Plan", Meta.MANTENER, "Detalle"))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void notificaAlPacienteSiElPlanEsNuevoOSiSeActualiza() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario(UUID.randomUUID(), "ana@nutrigo.local", "Ana");
        when(usuarioRepositoryPort.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
        when(planNutricionalRepositoryPort.guardar(any(PlanNutricional.class))).thenAnswer(inv -> inv.getArgument(0));
        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);

        when(planNutricionalRepositoryPort.buscarPorUsuarioId(usuarioId)).thenReturn(Optional.empty());
        service.asignarPlan(usuarioId, "Plan", Meta.MANTENER, "x");
        PlanNutricional existente = new PlanNutricional(usuario, "Plan", Meta.MANTENER, "x");
        when(planNutricionalRepositoryPort.buscarPorUsuarioId(usuarioId)).thenReturn(Optional.of(existente));
        service.asignarPlan(usuarioId, "Plan 2", Meta.MANTENER, "y");

        verify(notificacionRepositoryPort, times(2)).guardar(captor.capture());
        assertThat(captor.getAllValues()).extracting(Notificacion::getTipo)
                .containsExactly(TipoNotificacion.PLAN_ASIGNADO, TipoNotificacion.PLAN_ACTUALIZADO);
        assertThat(captor.getAllValues()).allSatisfy(n -> assertThat(n.getUsuario()).isSameAs(usuario));
    }
}
