package br.com.clinicaveterinaria.service;

import br.com.clinicaveterinaria.dto.VacinaTratamentoDTO;
import br.com.clinicaveterinaria.exception.ResourceNotFoundException;
import br.com.clinicaveterinaria.model.Pet;
import br.com.clinicaveterinaria.model.VacinaTratamento;
import br.com.clinicaveterinaria.repository.VacinaTratamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VacinaTratamentoService {

    private final VacinaTratamentoRepository vacinaRepository;
    private final PetService petService;

    public VacinaTratamentoService(VacinaTratamentoRepository vacinaRepository, PetService petService) {
        this.vacinaRepository = vacinaRepository;
        this.petService = petService;
    }

    @Transactional(readOnly = true)
    public List<VacinaTratamentoDTO.Response> listarPorPet(Long petId) {
        return vacinaRepository.findByPetIdOrderByDataAplicacaoDesc(petId).stream()
                .map(VacinaTratamentoDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public VacinaTratamentoDTO.Response buscarPorId(Long id) {
        VacinaTratamento v = obterEntidade(id);
        return VacinaTratamentoDTO.Response.fromEntity(v);
    }

    @Transactional(readOnly = true)
    public VacinaTratamento obterEntidade(Long id) {
        return vacinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacina / Imunização", id));
    }

    @Transactional
    public VacinaTratamentoDTO.Response cadastrar(VacinaTratamentoDTO.Request request) {
        Pet pet = petService.obterEntidade(request.petId());

        VacinaTratamento vacina = VacinaTratamento.builder()
                .pet(pet)
                .nome(request.nome().trim())
                .dataAplicacao(request.dataAplicacao())
                .dataProximaDose(request.dataProximaDose())
                .lote(request.lote() != null ? request.lote().trim() : null)
                .veterinarioResponsavel(request.veterinarioResponsavel() != null ? request.veterinarioResponsavel().trim() : null)
                .observacoes(request.observacoes())
                .build();

        VacinaTratamento salva = vacinaRepository.save(vacina);
        return VacinaTratamentoDTO.Response.fromEntity(salva);
    }

    @Transactional
    public void remover(Long id) {
        VacinaTratamento v = obterEntidade(id);
        vacinaRepository.delete(v);
    }
}
