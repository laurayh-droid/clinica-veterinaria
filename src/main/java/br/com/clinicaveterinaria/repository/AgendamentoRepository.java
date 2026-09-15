package br.com.clinicaveterinaria.repository;

import br.com.clinicaveterinaria.enums.StatusAgendamento;
import br.com.clinicaveterinaria.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByDataOrderByHoraAsc(LocalDate data);

    List<Agendamento> findByDataAndStatusOrderByHoraAsc(LocalDate data, StatusAgendamento status);

    long countByData(LocalDate data);

    long countByDataAndStatus(LocalDate data, StatusAgendamento status);

    List<Agendamento> findByPetIdOrderByDataDescHoraDesc(Long petId);

    List<Agendamento> findByProfissionalIdAndDataOrderByHoraAsc(Long profissionalId, LocalDate data);

    @Query("SELECT a FROM Agendamento a JOIN FETCH a.pet p JOIN FETCH a.tutor t LEFT JOIN FETCH a.profissional " +
           "WHERE a.data = :data ORDER BY a.hora ASC")
    List<Agendamento> findFilaDoDia(@Param("data") LocalDate data);
}
