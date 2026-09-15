package br.com.clinicaveterinaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade JPA que representa um prontuário clínico preenchido por um Médico Veterinário com CRMV.
 */
@Entity
@Table(name = "tb_prontuarios_clinicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "pet")
public class ProntuarioClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O pet associado ao prontuário é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @NotNull(message = "O médico veterinário responsável é obrigatório.")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Funcionario veterinario;

    @NotNull(message = "A data e hora do atendimento é obrigatória.")
    @Column(nullable = false)
    private LocalDateTime dataAtendimento;

    @NotBlank(message = "A queixa clínica principal é obrigatória.")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String queixaPrincipal;

    @Column(length = 255)
    private String sinaisVitais;

    @Column(length = 255)
    private String mucosas;

    @Column(columnDefinition = "TEXT")
    private String exameFisico;

    @NotBlank(message = "O diagnóstico clínico ou suspeita diagnóstica é obrigatório.")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String hipoteseDiagnostica;

    @NotBlank(message = "A conduta terapêutica e prescrição médica é obrigatória.")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String condutaPrescricao;

    @Column(columnDefinition = "TEXT")
    private String receituarioEmitido;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        if (this.dataAtendimento == null) {
            this.dataAtendimento = LocalDateTime.now();
        }
    }
}
