package br.com.clinicaveterinaria.dto;

import br.com.clinicaveterinaria.enums.CategoriaPet;
import br.com.clinicaveterinaria.enums.SexoPet;
import br.com.clinicaveterinaria.model.Pet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class PetDTO {

    public record Request(
            @NotBlank(message = "O nome do pet é obrigatório.")
            String nome,

            @NotNull(message = "A categoria do pet é obrigatória.")
            CategoriaPet categoria,

            @NotBlank(message = "A espécie / raça é obrigatória.")
            String especieRaca,

            SexoPet sexo,
            String idade,
            String peso,
            String habitatObservacoes,

            @NotNull(message = "O ID do tutor é obrigatório.")
            Long tutorId
    ) {}

    public record Resumo(
            Long id,
            String nome,
            CategoriaPet categoria,
            String categoriaDescricao,
            String especieRaca,
            SexoPet sexo,
            String idade,
            String peso,
            String habitatObservacoes,
            int totalProntuariosClinicos,
            int totalGuiasManejo,
            int totalVacinas
    ) {
        public static Resumo fromEntity(Pet p) {
            return new Resumo(
                    p.getId(),
                    p.getNome(),
                    p.getCategoria(),
                    p.getCategoria() != null ? p.getCategoria().getDescricao() : "",
                    p.getEspecieRaca(),
                    p.getSexo(),
                    p.getIdade(),
                    p.getPeso(),
                    p.getHabitatObservacoes(),
                    p.getProntuarios() != null ? p.getProntuarios().size() : 0,
                    p.getGuiasManejo() != null ? p.getGuiasManejo().size() : 0,
                    p.getVacinas() != null ? p.getVacinas().size() : 0
            );
        }
    }

    public record Response(
            Long id,
            String nome,
            CategoriaPet categoria,
            String categoriaDescricao,
            String especieRaca,
            SexoPet sexo,
            String sexoDescricao,
            String idade,
            String peso,
            String habitatObservacoes,
            Long tutorId,
            String tutorNome,
            String tutorTelefone,
            int totalProntuariosClinicos,
            int totalGuiasManejo,
            int totalVacinas,
            LocalDateTime dataCadastro
    ) {
        public static Response fromEntity(Pet p) {
            return new Response(
                    p.getId(),
                    p.getNome(),
                    p.getCategoria(),
                    p.getCategoria() != null ? p.getCategoria().getDescricao() : "",
                    p.getEspecieRaca(),
                    p.getSexo(),
                    p.getSexo() != null ? p.getSexo().getDescricao() : "",
                    p.getIdade(),
                    p.getPeso(),
                    p.getHabitatObservacoes(),
                    p.getTutor() != null ? p.getTutor().getId() : null,
                    p.getTutor() != null ? p.getTutor().getNome() : "",
                    p.getTutor() != null ? p.getTutor().getTelefone() : "",
                    p.getProntuarios() != null ? p.getProntuarios().size() : 0,
                    p.getGuiasManejo() != null ? p.getGuiasManejo().size() : 0,
                    p.getVacinas() != null ? p.getVacinas().size() : 0,
                    p.getDataCadastro()
            );
        }
    }
}
