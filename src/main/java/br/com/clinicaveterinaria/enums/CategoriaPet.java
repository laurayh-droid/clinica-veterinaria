package br.com.clinicaveterinaria.enums;

/**
 * Categoria biológica e de classificação do pet.
 */
public enum CategoriaPet {
    DOMESTICO("Doméstico"),
    SILVESTRE("Silvestre"),
    EXOTICO("Exótico");

    private final String descricao;

    CategoriaPet(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
