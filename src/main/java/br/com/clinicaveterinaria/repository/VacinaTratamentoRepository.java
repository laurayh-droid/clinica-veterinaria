package br.com.clinicaveterinaria.repository;

import br.com.clinicaveterinaria.model.VacinaTratamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VacinaTratamentoRepository extends JpaRepository<VacinaTratamento, Long> {

    List<VacinaTratamento> findByPetIdOrderByDataAplicacaoDesc(Long petId);

    List<VacinaTratamento> findByDataProximaDoseBetweenOrderByDataProximaDoseAsc(LocalDate inicio, LocalDate fim);
}
