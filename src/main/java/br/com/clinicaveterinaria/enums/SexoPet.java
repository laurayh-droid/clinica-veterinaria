package br.com.clinicaveterinaria.enums;

/**
 * Sexo do animal atendido.
 */
public enum SexoPet {
    MACHO("Macho"),
    FEMEA("Fêmea"),
    INDEFINIDO("Indefinido / Não sexado");

    private final String descricao;

    SexoPet(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
