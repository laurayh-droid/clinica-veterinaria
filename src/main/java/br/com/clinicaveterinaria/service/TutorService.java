package br.com.clinicaveterinaria.service;

import br.com.clinicaveterinaria.dto.TutorDTO;
import br.com.clinicaveterinaria.enums.CategoriaPet;
import br.com.clinicaveterinaria.exception.RegraDeNegocioException;
import br.com.clinicaveterinaria.exception.ResourceNotFoundException;
import br.com.clinicaveterinaria.model.Pet;
import br.com.clinicaveterinaria.model.Tutor;
import br.com.clinicaveterinaria.repository.TutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;

    public TutorService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    @Transactional(readOnly = true)
    public List<TutorDTO.Response> listarTodos() {
        return tutorRepository.findAllComPets().stream()
                .map(TutorDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TutorDTO.Response> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }
        return tutorRepository.buscarPorNomeOuCpf(termo.trim()).stream()
                .map(TutorDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TutorDTO.Response buscarPorId(Long id) {
        Tutor t = obterEntidade(id);
        return TutorDTO.Response.fromEntity(t);
    }

    @Transactional(readOnly = true)
    public Tutor obterEntidade(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor", id));
    }

    @Transactional
    public TutorDTO.Response cadastrar(TutorDTO.Request request) {
        if (tutorRepository.existsByCpf(request.cpf())) {
            throw new RegraDeNegocioException("Já existe um tutor cadastrado com o CPF " + request.cpf());
        }

        Tutor tutor = Tutor.builder()
                .nome(request.nome().trim())
                .cpf(request.cpf().trim())
                .telefone(request.telefone().trim())
                .endereco(request.endereco() != null ? request.endereco().trim() : null)
                .build();

        // Se veio pet inicial no cadastro do tutor
        if (request.primeiroPet() != null && request.primeiroPet().nome() != null && !request.primeiroPet().nome().isBlank()) {
            Pet pet = Pet.builder()
                    .nome(request.primeiroPet().nome().trim())
                    .categoria(request.primeiroPet().categoria() != null ? request.primeiroPet().categoria() : CategoriaPet.DOMESTICO)
                    .especieRaca(request.primeiroPet().especieRaca() != null ? request.primeiroPet().especieRaca().trim() : "Não informada")
                    .sexo(request.primeiroPet().sexo())
                    .idade(request.primeiroPet().idade())
                    .peso(request.primeiroPet().peso())
                    .habitatObservacoes(request.primeiroPet().habitatObservacoes())
                    .tutor(tutor)
                    .build();

            tutor.adicionarPet(pet);
        }

        Tutor salvo = tutorRepository.save(tutor);
        return TutorDTO.Response.fromEntity(salvo);
    }

    @Transactional
    public TutorDTO.Response atualizar(Long id, TutorDTO.Request request) {
        Tutor tutor = obterEntidade(id);

        if (!tutor.getCpf().equals(request.cpf()) && tutorRepository.existsByCpf(request.cpf())) {
            throw new RegraDeNegocioException("O CPF informado já está cadastrado para outro tutor.");
        }

        tutor.setNome(request.nome().trim());
        tutor.setCpf(request.cpf().trim());
        tutor.setTelefone(request.telefone().trim());
        tutor.setEndereco(request.endereco() != null ? request.endereco().trim() : null);

        Tutor atualizado = tutorRepository.save(tutor);
        return TutorDTO.Response.fromEntity(atualizado);
    }

    @Transactional
    public void remover(Long id) {
        Tutor tutor = obterEntidade(id);
        tutorRepository.delete(tutor);
    }
}
