package br.com.clinicaveterinaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade JPA para registro de vacinas, vermífugos e imunizações do animal.
 */
@Entity
@Table(name = "tb_vacinas_tratamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "pet")
public class VacinaTratamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O pet associado é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @NotBlank(message = "O nome da vacina ou medicamento é obrigatório.")
    @Column(nullable = false, length = 150)
    private String nome;

    @NotNull(message = "A data da aplicação é obrigatória.")
    @Column(nullable = false)
    private LocalDate dataAplicacao;

    @Column
    private LocalDate dataProximaDose;

    @Column(length = 50)
    private String lote;

    @Column(length = 150)
    private String veterinarioResponsavel;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        if (this.dataAplicacao == null) {
            this.dataAplicacao = LocalDate.now();
        }
    }
}
