package br.com.clinicaveterinaria.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA que representa o Tutor / Responsável legal pelos animais cadastrados.
 */
@Entity
@Table(name = "tb_tutores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "pets")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do tutor é obrigatório.")
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "O CPF do tutor é obrigatório.")
    @Column(nullable = false, unique = true, length = 20)
    private String cpf;

    @NotBlank(message = "O telefone / WhatsApp do tutor é obrigatório.")
    @Column(nullable = false, length = 30)
    private String telefone;

    @Column(length = 255)
    private String endereco;

    @Builder.Default
    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Pet> pets = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        if (this.pets == null) {
            this.pets = new ArrayList<>();
        }
    }

    public void adicionarPet(Pet pet) {
        if (this.pets == null) {
            this.pets = new ArrayList<>();
        }
        this.pets.add(pet);
        pet.setTutor(this);
    }

    public void removerPet(Pet pet) {
        if (this.pets != null) {
            this.pets.remove(pet);
            pet.setTutor(null);
        }
    }
}
