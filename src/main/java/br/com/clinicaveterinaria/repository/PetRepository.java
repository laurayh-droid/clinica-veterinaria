package br.com.clinicaveterinaria.repository;

import br.com.clinicaveterinaria.enums.CategoriaPet;
import br.com.clinicaveterinaria.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByTutorId(Long tutorId);

    List<Pet> findByCategoria(CategoriaPet categoria);

    long countByCategoria(CategoriaPet categoria);

    @Query("SELECT p FROM Pet p JOIN FETCH p.tutor WHERE " +
           "LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(p.especieRaca) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Pet> buscarPorNomeOuEspecie(@Param("termo") String termo);

    @Query("SELECT p FROM Pet p JOIN FETCH p.tutor")
    List<Pet> findAllComTutor();
}
