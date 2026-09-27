package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.in.ConsultarReportesUseCase;
import com.nutrigo.backend.application.port.in.GenerarReporteUseCase;
import com.nutrigo.backend.application.port.out.PlanNutricionalRepositoryPort;
import com.nutrigo.backend.application.port.out.RegistroProgresoRepositoryPort;
import com.nutrigo.backend.application.port.out.ReporteRepositoryPort;
import com.nutrigo.backend.application.port.out.UsuarioRepositoryPort;
import com.nutrigo.backend.domain.exception.UsuarioNoEncontradoException;
import com.nutrigo.backend.domain.model.PlanNutricional;
import com.nutrigo.backend.domain.model.RegistroProgreso;
import com.nutrigo.backend.domain.model.Reporte;
import com.nutrigo.backend.domain.model.TipoReporte;
import com.nutrigo.backend.domain.model.Usuario;
import com.nutrigo.backend.domain.service.GeneradorContenidoReporte;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReporteApplicationService implements GenerarReporteUseCase, ConsultarReportesUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PlanNutricionalRepositoryPort planNutricionalRepositoryPort;
    private final RegistroProgresoRepositoryPort registroProgresoRepositoryPort;
    private final ReporteRepositoryPort reporteRepositoryPort;

    public ReporteApplicationService(UsuarioRepositoryPort usuarioRepositoryPort,
                                      PlanNutricionalRepositoryPort planNutricionalRepositoryPort,
                                      RegistroProgresoRepositoryPort registroProgresoRepositoryPort,
                                      ReporteRepositoryPort reporteRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.planNutricionalRepositoryPort = planNutricionalRepositoryPort;
        this.registroProgresoRepositoryPort = registroProgresoRepositoryPort;
        this.reporteRepositoryPort = reporteRepositoryPort;
    }

    @Override
    public Reporte generarReporte(UUID usuarioId) {
        Usuario usuario = usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("No existe un usuario con id " + usuarioId));

        List<RegistroProgreso> historial = registroProgresoRepositoryPort.buscarPorUsuarioOrdenadoDesc(usuarioId);
        PlanNutricional plan = planNutricionalRepositoryPort.buscarPorUsuarioId(usuarioId).orElse(null);

        String contenido = GeneradorContenidoReporte.generar(usuario, historial, plan);
        Reporte reporte = new Reporte(usuario, TipoReporte.RESUMEN_NUTRICIONAL, contenido);
        return reporteRepositoryPort.guardar(reporte);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reporte> consultarReportes(UUID usuarioId) {
        return reporteRepositoryPort.buscarPorUsuarioOrdenadoDesc(usuarioId);
    }
}
