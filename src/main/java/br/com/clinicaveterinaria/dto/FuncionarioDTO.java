package br.com.clinicaveterinaria.dto;

import br.com.clinicaveterinaria.enums.PerfilFuncionario;
import br.com.clinicaveterinaria.model.Funcionario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class FuncionarioDTO {

    public record Request(
            @NotBlank(message = "O nome é obrigatório.")
            String nome,

            @NotBlank(message = "O CPF é obrigatório.")
            String cpf,

            @NotBlank(message = "O e-mail é obrigatório.")
            @Email(message = "E-mail inválido.")
            String email,

            @NotBlank(message = "O telefone é obrigatório.")
            String telefone,

            @NotNull(message = "O cargo/perfil é obrigatório.")
            PerfilFuncionario cargo,

            String crmv
    ) {}

    public record Response(
            Long id,
            String nome,
            String cpf,
            String email,
            String telefone,
            PerfilFuncionario cargo,
            String cargoDescricao,
            String crmv,
            Boolean ativo,
            LocalDateTime dataCadastro
    ) {
        public static Response fromEntity(Funcionario f) {
            return new Response(
                    f.getId(),
                    f.getNome(),
                    f.getCpf(),
                    f.getEmail(),
                    f.getTelefone(),
                    f.getCargo(),
                    f.getCargo() != null ? f.getCargo().getDescricao() : "",
                    f.getCrmv(),
                    f.getAtivo(),
                    f.getDataCadastro()
            );
        }
    }
}
