package br.com.clinicaveterinaria.dto;

import br.com.clinicaveterinaria.enums.CategoriaPet;
import br.com.clinicaveterinaria.enums.StatusAgendamento;
import br.com.clinicaveterinaria.enums.TipoServico;
import br.com.clinicaveterinaria.model.Agendamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AgendamentoDTO {

    public record Request(
            @NotNull(message = "A data é obrigatória.")
            LocalDate data,

            @NotBlank(message = "O horário é obrigatório.")
            String hora,

            @NotNull(message = "O ID do pet é obrigatório.")
            Long petId,

            @NotNull(message = "O tipo de serviço é obrigatório.")
            TipoServico tipoServico,

            Long profissionalId,
            String observacoes
    ) {}

    public record StatusUpdateRequest(
            @NotNull(message = "O novo status é obrigatório.")
            StatusAgendamento status
    ) {}

    public record Response(
            Long id,
            LocalDate data,
            String hora,
            Long petId,
            String petNome,
            String petEspecieRaca,
            CategoriaPet petCategoria,
            String petCategoriaDescricao,
            Long tutorId,
            String tutorNome,
            String tutorTelefone,
            TipoServico tipoServico,
            String tipoServicoDescricao,
            Long profissionalId,
            String profissionalNome,
            String profissionalCargo,
            StatusAgendamento status,
            String statusDescricao,
            String observacoes,
            LocalDateTime dataCriacao
    ) {
        public static Response fromEntity(Agendamento a) {
            return new Response(
                    a.getId(),
                    a.getData(),
                    a.getHora(),
                    a.getPet() != null ? a.getPet().getId() : null,
                    a.getPet() != null ? a.getPet().getNome() : "",
                    a.getPet() != null ? a.getPet().getEspecieRaca() : "",
                    a.getPet() != null ? a.getPet().getCategoria() : null,
                    a.getPet() != null && a.getPet().getCategoria() != null ? a.getPet().getCategoria().getDescricao() : "",
                    a.getTutor() != null ? a.getTutor().getId() : null,
                    a.getTutor() != null ? a.getTutor().getNome() : "",
                    a.getTutor() != null ? a.getTutor().getTelefone() : "",
                    a.getTipoServico(),
                    a.getTipoServico() != null ? a.getTipoServico().getDescricao() : "",
                    a.getProfissional() != null ? a.getProfissional().getId() : null,
                    a.getProfissional() != null ? a.getProfissional().getNome() : "Não atribuído",
                    a.getProfissional() != null && a.getProfissional().getCargo() != null ? a.getProfissional().getCargo().getDescricao() : "",
                    a.getStatus(),
                    a.getStatus() != null ? a.getStatus().getDescricao() : "",
                    a.getObservacoes(),
                    a.getDataCriacao()
            );
        }
    }
}
