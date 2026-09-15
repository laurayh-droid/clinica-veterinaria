package br.com.clinicaveterinaria.service;

import br.com.clinicaveterinaria.dto.PetDTO;
import br.com.clinicaveterinaria.enums.CategoriaPet;
import br.com.clinicaveterinaria.exception.RegraDeNegocioException;
import br.com.clinicaveterinaria.exception.ResourceNotFoundException;
import br.com.clinicaveterinaria.model.Pet;
import br.com.clinicaveterinaria.model.Tutor;
import br.com.clinicaveterinaria.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final TutorService tutorService;

    public PetService(PetRepository petRepository, TutorService tutorService) {
        this.petRepository = petRepository;
        this.tutorService = tutorService;
    }

    @Transactional(readOnly = true)
    public List<PetDTO.Response> listarTodos() {
        return petRepository.findAllComTutor().stream()
                .map(PetDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PetDTO.Response> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }
        return petRepository.buscarPorNomeOuEspecie(termo.trim()).stream()
                .map(PetDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PetDTO.Response> listarPorCategoria(CategoriaPet categoria) {
        return petRepository.findByCategoria(categoria).stream()
                .map(PetDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PetDTO.Response> listarPorTutor(Long tutorId) {
        return petRepository.findByTutorId(tutorId).stream()
                .map(PetDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public PetDTO.Response buscarPorId(Long id) {
        Pet pet = obterEntidade(id);
        return PetDTO.Response.fromEntity(pet);
    }

    @Transactional(readOnly = true)
    public Pet obterEntidade(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet / Paciente", id));
    }

    @Transactional
    public PetDTO.Response cadastrar(PetDTO.Request request) {
        // RN01: Todo pet deve estar obrigatoriamente associado a um tutor cadastrado
        if (request.tutorId() == null) {
            throw new RegraDeNegocioException("É obrigatório vincular o pet a um tutor cadastrado (RN01).");
        }

        Tutor tutor = tutorService.obterEntidade(request.tutorId());

        Pet pet = Pet.builder()
                .nome(request.nome().trim())
                .categoria(request.categoria())
                .especieRaca(request.especieRaca().trim())
                .sexo(request.sexo())
                .idade(request.idade() != null ? request.idade().trim() : null)
                .peso(request.peso() != null ? request.peso().trim() : null)
                .habitatObservacoes(request.habitatObservacoes() != null ? request.habitatObservacoes().trim() : null)
                .tutor(tutor)
                .build();

        Pet salvo = petRepository.save(pet);
        return PetDTO.Response.fromEntity(salvo);
    }

    @Transactional
    public PetDTO.Response atualizar(Long id, PetDTO.Request request) {
        Pet pet = obterEntidade(id);

        if (request.tutorId() != null && !request.tutorId().equals(pet.getTutor().getId())) {
            Tutor novoTutor = tutorService.obterEntidade(request.tutorId());
            pet.setTutor(novoTutor);
        }

        pet.setNome(request.nome().trim());
        pet.setCategoria(request.categoria());
        pet.setEspecieRaca(request.especieRaca().trim());
        pet.setSexo(request.sexo());
        pet.setIdade(request.idade() != null ? request.idade().trim() : null);
        pet.setPeso(request.peso() != null ? request.peso().trim() : null);
        pet.setHabitatObservacoes(request.habitatObservacoes() != null ? request.habitatObservacoes().trim() : null);

        Pet atualizado = petRepository.save(pet);
        return PetDTO.Response.fromEntity(atualizado);
    }

    @Transactional
    public void remover(Long id) {
        Pet pet = obterEntidade(id);
        petRepository.delete(pet);
    }
}
