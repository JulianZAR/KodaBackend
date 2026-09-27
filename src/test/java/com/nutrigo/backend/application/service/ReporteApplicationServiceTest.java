package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.out.PlanNutricionalRepositoryPort;
import com.nutrigo.backend.application.port.out.RegistroProgresoRepositoryPort;
import com.nutrigo.backend.application.port.out.ReporteRepositoryPort;
import com.nutrigo.backend.application.port.out.UsuarioRepositoryPort;
import com.nutrigo.backend.domain.exception.UsuarioNoEncontradoException;
import com.nutrigo.backend.domain.model.Reporte;
import com.nutrigo.backend.domain.model.TipoReporte;
import com.nutrigo.backend.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteApplicationServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PlanNutricionalRepositoryPort planNutricionalRepositoryPort;

    @Mock
    private RegistroProgresoRepositoryPort registroProgresoRepositoryPort;

    @Mock
    private ReporteRepositoryPort reporteRepositoryPort;

    private ReporteApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ReporteApplicationService(usuarioRepositoryPort, planNutricionalRepositoryPort,
                registroProgresoRepositoryPort, reporteRepositoryPort);
    }

    @Test
    void generaUnReporteDeTipoResumenNutricionalConElContenidoDelUsuario() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario(UUID.randomUUID(), "ana@nutrigo.local", "Ana");
        when(usuarioRepositoryPort.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
        when(registroProgresoRepositoryPort.buscarPorUsuarioOrdenadoDesc(usuarioId)).thenReturn(List.of());
        when(planNutricionalRepositoryPort.buscarPorUsuarioId(usuarioId)).thenReturn(Optional.empty());
        when(reporteRepositoryPort.guardar(any(Reporte.class))).thenAnswer(inv -> inv.getArgument(0));

        Reporte reporte = service.generarReporte(usuarioId);

        assertThat(reporte.getTipo()).isEqualTo(TipoReporte.RESUMEN_NUTRICIONAL);
        assertThat(reporte.getContenido()).contains("Ana").contains("sin plan asignado");
    }

    @Test
    void fallaSiElUsuarioNoExiste() {
        UUID usuarioId = UUID.randomUUID();
        when(usuarioRepositoryPort.buscarPorId(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.generarReporte(usuarioId))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void consultaElHistorialDeReportesDelUsuario() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario(UUID.randomUUID(), "ana@nutrigo.local", "Ana");
        Reporte reporte = new Reporte(usuario, TipoReporte.RESUMEN_NUTRICIONAL, "contenido");
        when(reporteRepositoryPort.buscarPorUsuarioOrdenadoDesc(usuarioId)).thenReturn(List.of(reporte));

        List<Reporte> reportes = service.consultarReportes(usuarioId);

        assertThat(reportes).containsExactly(reporte);
    }
}
