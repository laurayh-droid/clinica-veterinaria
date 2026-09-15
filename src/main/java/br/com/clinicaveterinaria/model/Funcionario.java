package br.com.clinicaveterinaria.model;

import br.com.clinicaveterinaria.enums.PerfilFuncionario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade JPA que representa um membro do corpo clínico e equipe da clínica veterinária.
 */
@Entity
@Table(name = "tb_funcionarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do profissional é obrigatório.")
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @Column(nullable = false, unique = true, length = 20)
    private String cpf;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @NotBlank(message = "O telefone é obrigatório.")
    @Column(nullable = false, length = 30)
    private String telefone;

    @NotNull(message = "O cargo/perfil é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PerfilFuncionario cargo;

    @Column(length = 30)
    private String crmv;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        if (this.ativo == null) {
            this.ativo = true;
        }
    }
}
