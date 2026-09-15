package br.com.clinicaveterinaria.repository;

import br.com.clinicaveterinaria.model.ProntuarioClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProntuarioClinicoRepository extends JpaRepository<ProntuarioClinico, Long> {

    @Query("SELECT p FROM ProntuarioClinico p JOIN FETCH p.veterinario JOIN FETCH p.pet pet JOIN FETCH pet.tutor " +
           "WHERE p.pet.id = :petId ORDER BY p.dataAtendimento DESC")
    List<ProntuarioClinico> findByPetIdOrderByDataAtendimentoDesc(@Param("petId") Long petId);

    List<ProntuarioClinico> findByVeterinarioIdOrderByDataAtendimentoDesc(Long veterinarioId);
}
