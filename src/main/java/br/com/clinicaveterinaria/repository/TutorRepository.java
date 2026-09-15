package br.com.clinicaveterinaria.repository;

import br.com.clinicaveterinaria.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    Optional<Tutor> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    @Query("SELECT DISTINCT t FROM Tutor t LEFT JOIN FETCH t.pets WHERE " +
           "LOWER(t.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "t.cpf LIKE CONCAT('%', :termo, '%')")
    List<Tutor> buscarPorNomeOuCpf(@Param("termo") String termo);

    @Query("SELECT DISTINCT t FROM Tutor t LEFT JOIN FETCH t.pets")
    List<Tutor> findAllComPets();
}
