package br.com.clinicaveterinaria.enums;

/**
 * Perfis de atuação e cargos dos colaboradores da clínica.
 */
public enum PerfilFuncionario {
    ADMINISTRADOR("Administrador Geral"),
    VETERINARIO("Médico(a) Veterinário(a)"),
    ESPECIALISTA_SILVESTRES("Adestrador(a) / Especialista em Silvestres"),
    ATENDENTE("Recepcionista / Atendente");

    private final String descricao;

    PerfilFuncionario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
