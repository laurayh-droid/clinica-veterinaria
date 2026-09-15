package br.com.clinicaveterinaria.model;

import br.com.clinicaveterinaria.enums.StatusAgendamento;
import br.com.clinicaveterinaria.enums.TipoServico;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade JPA que representa um agendamento na recepção e controle da fila diária.
 */
@Entity
@Table(name = "tb_agendamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data do agendamento é obrigatória.")
    @Column(nullable = false)
    private LocalDate data;

    @NotBlank(message = "O horário do agendamento é obrigatório.")
    @Column(nullable = false, length = 10)
    private String hora;

    @NotNull(message = "O paciente pet é obrigatório.")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @NotNull(message = "O tutor é obrigatório.")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @NotNull(message = "O tipo de serviço é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoServico tipoServico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profissional_id")
    private Funcionario profissional;

    @NotNull(message = "O status do agendamento é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatusAgendamento status = StatusAgendamento.AGENDADO;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusAgendamento.AGENDADO;
        }
    }
}
