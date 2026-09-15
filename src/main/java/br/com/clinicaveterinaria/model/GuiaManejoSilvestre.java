package br.com.clinicaveterinaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade JPA que representa um Guia de Manejo Silvestre e Consultoria Comportamental.
 */
@Entity
@Table(name = "tb_guias_manejo_silvestre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "pet")
public class GuiaManejoSilvestre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O pet associado ao guia é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @NotNull(message = "O especialista em silvestres/adestrador responsável é obrigatório.")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "especialista_id", nullable = false)
    private Funcionario especialista;

    @NotNull(message = "A data do atendimento de manejo é obrigatória.")
    @Column(nullable = false)
    private LocalDateTime dataAtendimento;

    @NotBlank(message = "A queixa comportamental / estresse é obrigatória.")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String queixaComportamental;

    @Column(length = 255)
    private String dimensoesRecinto;

    @Column(length = 255)
    private String parametrosIluminacao;

    @Column(length = 255)
    private String parametrosTemperatura;

    @Column(length = 255)
    private String tipoSubstrato;

    @Column(columnDefinition = "TEXT")
    private String enriquecimentoAmbiental;

    @Column(columnDefinition = "TEXT")
    private String planoTreinamentoAdestramento;

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
