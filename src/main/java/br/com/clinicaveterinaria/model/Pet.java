package br.com.clinicaveterinaria.model;

import br.com.clinicaveterinaria.enums.CategoriaPet;
import br.com.clinicaveterinaria.enums.SexoPet;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA que representa o Pet paciente (doméstico, silvestre ou exótico).
 */
@Entity
@Table(name = "tb_pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"tutor", "prontuarios", "guiasManejo", "vacinas"})
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do pet é obrigatório.")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotNull(message = "A categoria do pet é obrigatória.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaPet categoria;

    @NotBlank(message = "A espécie / raça é obrigatória.")
    @Column(nullable = false, length = 100)
    private String especieRaca;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SexoPet sexo;

    @Column(length = 50)
    private String idade;

    @Column(length = 50)
    private String peso;

    @Column(columnDefinition = "TEXT")
    private String habitatObservacoes;

    @NotNull(message = "O tutor é obrigatório para cadastrar um pet.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    @JsonBackReference
    private Tutor tutor;

    @Builder.Default
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProntuarioClinico> prontuarios = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<GuiaManejoSilvestre> guiasManejo = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<VacinaTratamento> vacinas = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        if (this.prontuarios == null) this.prontuarios = new ArrayList<>();
        if (this.guiasManejo == null) this.guiasManejo = new ArrayList<>();
        if (this.vacinas == null) this.vacinas = new ArrayList<>();
    }
}
