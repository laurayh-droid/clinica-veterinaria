package br.com.clinicaveterinaria.model;

import br.com.clinicaveterinaria.enums.StatusAgendamento;
import br.com.clinicaveterinaria.enums.TipoServico;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

    @Column(length = 10)
    private String horaFim;

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
    @PreUpdate
    protected void onPersistOrUpdate() {
        if (this.dataCriacao == null) {
            this.dataCriacao = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = StatusAgendamento.AGENDADO;
        }
        if (this.hora != null && (this.horaFim == null || this.horaFim.isBlank())) {
            this.horaFim = calcularHoraFim(this.hora, 15);
        }
    }

    /**
     * Calcula o horário de término com base no horário de início e duração em minutos.
     */
    public static String calcularHoraFim(String horaInicio, int duracaoMinutos) {
        if (horaInicio == null || horaInicio.isBlank()) {
            return null;
        }
        String limpa = horaInicio.trim();
        if (limpa.length() == 4 && limpa.charAt(1) == ':') {
            limpa = "0" + limpa;
        } else if (limpa.length() > 5) {
            limpa = limpa.substring(0, 5);
        }
        LocalTime t = LocalTime.parse(limpa, DateTimeFormatter.ofPattern("HH:mm"));
        return t.plusMinutes(duracaoMinutos).format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
