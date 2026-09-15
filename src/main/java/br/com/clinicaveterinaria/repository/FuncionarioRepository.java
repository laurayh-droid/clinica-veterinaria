package br.com.clinicaveterinaria.repository;

import br.com.clinicaveterinaria.enums.PerfilFuncionario;
import br.com.clinicaveterinaria.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Optional<Funcionario> findByCpf(String cpf);

    Optional<Funcionario> findByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    List<Funcionario> findByAtivoTrue();

    List<Funcionario> findByCargoAndAtivoTrue(PerfilFuncionario cargo);

    List<Funcionario> findByNomeContainingIgnoreCaseOrCpfContaining(String nome, String cpf);
}
