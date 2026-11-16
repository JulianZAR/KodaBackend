package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.in.ConsultarPacientesUseCase.ResumenPaciente;
import com.nutrigo.backend.application.port.out.PlanNutricionalRepositoryPort;
import com.nutrigo.backend.application.port.out.RegistroProgresoRepositoryPort;
import com.nutrigo.backend.application.port.out.UsuarioRepositoryPort;
import com.nutrigo.backend.domain.exception.UsuarioNoEncontradoException;
import com.nutrigo.backend.domain.model.CategoriaImc;
import com.nutrigo.backend.domain.model.Meta;
import com.nutrigo.backend.domain.model.PlanNutricional;
import com.nutrigo.backend.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacientesApplicationServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PlanNutricionalRepositoryPort planNutricionalRepositoryPort;

    @Mock
    private RegistroProgresoRepositoryPort registroProgresoRepositoryPort;

    private PacientesApplicationService service;

    @BeforeEach
    void setUp() {
        service = new PacientesApplicationService(usuarioRepositoryPort, planNutricionalRepositoryPort,
                registroProgresoRepositoryPort);
    }

    @Test
    void listaPacientesConSuImcYSuPlanVigente() {
        Usuario conDatos = usuario("Laura");
        conDatos.actualizarEstaturaYMeta(BigDecimal.valueOf(165), Meta.PERDER_PESO);
        conDatos.actualizarPeso(BigDecimal.valueOf(75.2));
        Usuario sinDatos = usuario("Carlos");
        PlanNutricional plan = new PlanNutricional(conDatos, "Plan fase 1", Meta.PERDER_PESO, "Detalle");

        when(usuarioRepositoryPort.listarTodos()).thenReturn(List.of(sinDatos, conDatos));
        when(planNutricionalRepositoryPort.listarTodos()).thenReturn(List.of(plan));

        List<ResumenPaciente> pacientes = service.listarPacientes();

        ResumenPaciente carlos = pacientes.get(0);
        assertThat(carlos.imc()).isNull();
        assertThat(carlos.planVigente()).isNull();

        ResumenPaciente laura = pacientes.get(1);
        assertThat(laura.imc().valor()).isEqualByComparingTo("27.62");
        assertThat(laura.imc().categoria()).isEqualTo(CategoriaImc.SOBREPESO);
        assertThat(laura.planVigente()).isSameAs(plan);
    }

    @Test
    void fallaSiElPacienteNoExiste() {
        UUID id = UUID.randomUUID();
        when(usuarioRepositoryPort.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPaciente(id)).isInstanceOf(UsuarioNoEncontradoException.class);
        assertThatThrownBy(() -> service.consultarHistorialDePaciente(id))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    private static Usuario usuario(String nombre) {
        Usuario usuario = new Usuario(UUID.randomUUID(), nombre.toLowerCase() + "@nutrigo.local", nombre);
        ReflectionTestUtils.setField(usuario, "id", UUID.randomUUID());
        return usuario;
    }
}
