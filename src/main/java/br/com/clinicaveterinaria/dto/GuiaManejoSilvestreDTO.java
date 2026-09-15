package br.com.clinicaveterinaria.dto;

import br.com.clinicaveterinaria.model.GuiaManejoSilvestre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class GuiaManejoSilvestreDTO {

    public record Request(
            @NotNull(message = "O ID do pet é obrigatório.")
            Long petId,

            @NotNull(message = "O ID do especialista/adestrador é obrigatório.")
            Long especialistaId,

            @NotBlank(message = "A queixa comportamental / estresse é obrigatória.")
            String queixaComportamental,

            String dimensoesRecinto,
            String parametrosIluminacao,
            String parametrosTemperatura,
            String tipoSubstrato,
            String enriquecimentoAmbiental,
            String planoTreinamentoAdestramento
    ) {}

    public record Response(
            Long id,
            Long petId,
            String petNome,
            String petEspecieRaca,
            String petCategoria,
            String tutorNome,
            Long especialistaId,
            String especialistaNome,
            String especialistaCargo,
            LocalDateTime dataAtendimento,
            String queixaComportamental,
            String dimensoesRecinto,
            String parametrosIluminacao,
            String parametrosTemperatura,
            String tipoSubstrato,
            String enriquecimentoAmbiental,
            String planoTreinamentoAdestramento,
            LocalDateTime dataCriacao
    ) {
        public static Response fromEntity(GuiaManejoSilvestre g) {
            return new Response(
                    g.getId(),
                    g.getPet() != null ? g.getPet().getId() : null,
                    g.getPet() != null ? g.getPet().getNome() : "",
                    g.getPet() != null ? g.getPet().getEspecieRaca() : "",
                    g.getPet() != null && g.getPet().getCategoria() != null ? g.getPet().getCategoria().getDescricao() : "",
                    g.getPet() != null && g.getPet().getTutor() != null ? g.getPet().getTutor().getNome() : "",
                    g.getEspecialista() != null ? g.getEspecialista().getId() : null,
                    g.getEspecialista() != null ? g.getEspecialista().getNome() : "",
                    g.getEspecialista() != null && g.getEspecialista().getCargo() != null ? g.getEspecialista().getCargo().getDescricao() : "",
                    g.getDataAtendimento(),
                    g.getQueixaComportamental(),
                    g.getDimensoesRecinto(),
                    g.getParametrosIluminacao(),
                    g.getParametrosTemperatura(),
                    g.getTipoSubstrato(),
                    g.getEnriquecimentoAmbiental(),
                    g.getPlanoTreinamentoAdestramento(),
                    g.getDataCriacao()
            );
        }
    }
}
