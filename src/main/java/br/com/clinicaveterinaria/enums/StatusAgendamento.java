package br.com.clinicaveterinaria.enums;

/**
 * Status do ciclo de vida de um atendimento na recepção e fila clínica.
 */
public enum StatusAgendamento {
    AGENDADO("Agendado"),
    AGUARDANDO("Aguardando na Recepção"),
    EM_ATENDIMENTO("Em Atendimento"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusAgendamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
