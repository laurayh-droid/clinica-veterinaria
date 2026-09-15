package br.com.clinicaveterinaria.controller;

import br.com.clinicaveterinaria.dto.DashboardMetricasDTO;
import br.com.clinicaveterinaria.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard & Métricas", description = "Métricas em tempo real da recepção, fila de espera e distribuição de pacientes")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/metricas")
    @Operation(summary = "Obter indicadores do dia", description = "Retorna contadores de atendimentos de hoje, fila aguardando e distribuição silvestres vs domésticos")
    public ResponseEntity<DashboardMetricasDTO> obterMetricas() {
        return ResponseEntity.ok(dashboardService.obterMetricasHoje());
    }
}
