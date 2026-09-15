package br.com.clinicaveterinaria.dto;

import br.com.clinicaveterinaria.model.ProntuarioClinico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ProntuarioClinicoDTO {

    public record Request(
            @NotNull(message = "O ID do pet é obrigatório.")
            Long petId,

            @NotNull(message = "O ID do médico veterinário é obrigatório.")
            Long veterinarioId,

            @NotBlank(message = "A queixa clínica principal é obrigatória.")
            String queixaPrincipal,

            String sinaisVitais,
            String mucosas,
            String exameFisico,

            @NotBlank(message = "A suspeita/diagnóstico clínico é obrigatório.")
            String hipoteseDiagnostica,

            @NotBlank(message = "A conduta e prescrição médica é obrigatória.")
            String condutaPrescricao,

            String receituarioEmitido
    ) {}

    public record Response(
            Long id,
            Long petId,
            String petNome,
            String petEspecieRaca,
            String tutorNome,
            Long veterinarioId,
            String veterinarioNome,
            String veterinarioCrmv,
            LocalDateTime dataAtendimento,
            String queixaPrincipal,
            String sinaisVitais,
            String mucosas,
            String exameFisico,
            String hipoteseDiagnostica,
            String condutaPrescricao,
            String receituarioEmitido,
            LocalDateTime dataCriacao
    ) {
        public static Response fromEntity(ProntuarioClinico p) {
            return new Response(
                    p.getId(),
                    p.getPet() != null ? p.getPet().getId() : null,
                    p.getPet() != null ? p.getPet().getNome() : "",
                    p.getPet() != null ? p.getPet().getEspecieRaca() : "",
                    p.getPet() != null && p.getPet().getTutor() != null ? p.getPet().getTutor().getNome() : "",
                    p.getVeterinario() != null ? p.getVeterinario().getId() : null,
                    p.getVeterinario() != null ? p.getVeterinario().getNome() : "",
                    p.getVeterinario() != null ? p.getVeterinario().getCrmv() : "",
                    p.getDataAtendimento(),
                    p.getQueixaPrincipal(),
                    p.getSinaisVitais(),
                    p.getMucosas(),
                    p.getExameFisico(),
                    p.getHipoteseDiagnostica(),
                    p.getCondutaPrescricao(),
                    p.getReceituarioEmitido(),
                    p.getDataCriacao()
            );
        }
    }

    public record ReceitaResponse(
            String nomeClinica,
            String veterinarioNome,
            String veterinarioCrmv,
            String petNome,
            String petEspecieRaca,
            String tutorNome,
            LocalDateTime dataEmissao,
            String prescricaoMedicamentosa
    ) {}
}
