package br.com.clinicaveterinaria.dto;

import br.com.clinicaveterinaria.model.VacinaTratamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VacinaTratamentoDTO {

    public record Request(
            @NotNull(message = "O ID do pet é obrigatório.")
            Long petId,

            @NotBlank(message = "O nome da vacina / tratamento é obrigatório.")
            String nome,

            @NotNull(message = "A data da aplicação é obrigatória.")
            LocalDate dataAplicacao,

            LocalDate dataProximaDose,
            String lote,
            String veterinarioResponsavel,
            String observacoes
    ) {}

    public record Response(
            Long id,
            Long petId,
            String petNome,
            String nome,
            LocalDate dataAplicacao,
            LocalDate dataProximaDose,
            String lote,
            String veterinarioResponsavel,
            String observacoes,
            LocalDateTime dataCadastro
    ) {
        public static Response fromEntity(VacinaTratamento v) {
            return new Response(
                    v.getId(),
                    v.getPet() != null ? v.getPet().getId() : null,
                    v.getPet() != null ? v.getPet().getNome() : "",
                    v.getNome(),
                    v.getDataAplicacao(),
                    v.getDataProximaDose(),
                    v.getLote(),
                    v.getVeterinarioResponsavel(),
                    v.getObservacoes(),
                    v.getDataCadastro()
            );
        }
    }
}
