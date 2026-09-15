package br.com.clinicaveterinaria.enums;

/**
 * Tipos de serviços clínicos e comportamentais prestados pela clínica.
 */
public enum TipoServico {
    CONSULTA_CLINICA("Consulta Clínica Veterinária"),
    CONSULTORIA_MANEJO("Consultoria de Manejo & Adestramento Silvestre"),
    CHECKUP_NUTRICIONAL("Check-up Nutricional e Recinto"),
    VACINACAO("Vacinação & Imunização"),
    RETORNO("Retorno Clínico / Reavaliação"),
    OUTRO("Outros Procedimentos");

    private final String descricao;

    TipoServico(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
