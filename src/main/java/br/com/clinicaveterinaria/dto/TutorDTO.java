package br.com.clinicaveterinaria.dto;

import br.com.clinicaveterinaria.enums.CategoriaPet;
import br.com.clinicaveterinaria.enums.SexoPet;
import br.com.clinicaveterinaria.model.Tutor;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class TutorDTO {

    public record Request(
            @NotBlank(message = "O nome do tutor é obrigatório.")
            String nome,

            @NotBlank(message = "O CPF do tutor é obrigatório.")
            String cpf,

            @NotBlank(message = "O telefone / WhatsApp é obrigatório.")
            String telefone,

            String endereco,

            // Opcional para cadastro em lote junto com o primeiro Pet
            PetCadastroInicialDTO primeiroPet
    ) {}

    public record PetCadastroInicialDTO(
            @NotBlank(message = "O nome do pet é obrigatório.")
            String nome,
            CategoriaPet categoria,
            String especieRaca,
            SexoPet sexo,
            String idade,
            String peso,
            String habitatObservacoes
    ) {}

    public record Response(
            Long id,
            String nome,
            String cpf,
            String telefone,
            String endereco,
            List<PetDTO.Resumo> pets,
            LocalDateTime dataCadastro
    ) {
        public static Response fromEntity(Tutor t) {
            List<PetDTO.Resumo> petResumos = t.getPets() != null
                    ? t.getPets().stream().map(PetDTO.Resumo::fromEntity).toList()
                    : Collections.emptyList();

            return new Response(
                    t.getId(),
                    t.getNome(),
                    t.getCpf(),
                    t.getTelefone(),
                    t.getEndereco(),
                    petResumos,
                    t.getDataCadastro()
            );
        }
    }
}
