package br.com.clinicaveterinaria.dto;

import java.time.LocalDate;

/**
 * DTO para agregação de métricas da clínica e fila de espera do dia.
 */
public record DashboardMetricasDTO(
        LocalDate dataReferencia,
        long totalConsultasHoje,
        long totalAguardandoFila,
        long totalEmAtendimento,
        long totalFinalizadosHoje,
        long totalAnimaisSilvestresExoticos,
        long totalAnimaisDomesticos,
        long totalTutoresCadastrados,
        long totalProfissionaisAtivos
) {}
