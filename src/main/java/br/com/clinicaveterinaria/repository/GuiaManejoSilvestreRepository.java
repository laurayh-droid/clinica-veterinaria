package br.com.clinicaveterinaria.repository;

import br.com.clinicaveterinaria.model.GuiaManejoSilvestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuiaManejoSilvestreRepository extends JpaRepository<GuiaManejoSilvestre, Long> {

    @Query("SELECT g FROM GuiaManejoSilvestre g JOIN FETCH g.especialista JOIN FETCH g.pet pet JOIN FETCH pet.tutor " +
           "WHERE g.pet.id = :petId ORDER BY g.dataAtendimento DESC")
    List<GuiaManejoSilvestre> findByPetIdOrderByDataAtendimentoDesc(@Param("petId") Long petId);

    List<GuiaManejoSilvestre> findByEspecialistaIdOrderByDataAtendimentoDesc(Long especialistaId);
}
