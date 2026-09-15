package br.com.clinicaveterinaria.service;

import br.com.clinicaveterinaria.dto.ProntuarioClinicoDTO;
import br.com.clinicaveterinaria.enums.PerfilFuncionario;
import br.com.clinicaveterinaria.enums.StatusAgendamento;
import br.com.clinicaveterinaria.exception.RegraDeNegocioException;
import br.com.clinicaveterinaria.exception.ResourceNotFoundException;
import br.com.clinicaveterinaria.model.Agendamento;
import br.com.clinicaveterinaria.model.Funcionario;
import br.com.clinicaveterinaria.model.Pet;
import br.com.clinicaveterinaria.model.ProntuarioClinico;
import br.com.clinicaveterinaria.repository.AgendamentoRepository;
import br.com.clinicaveterinaria.repository.ProntuarioClinicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProntuarioClinicoService {

    private final ProntuarioClinicoRepository prontuarioRepository;
    private final PetService petService;
    private final FuncionarioService funcionarioService;
    private final AgendamentoRepository agendamentoRepository;

    public ProntuarioClinicoService(ProntuarioClinicoRepository prontuarioRepository,
                                    PetService petService,
                                    FuncionarioService funcionarioService,
                                    AgendamentoRepository agendamentoRepository) {
        this.prontuarioRepository = prontuarioRepository;
        this.petService = petService;
        this.funcionarioService = funcionarioService;
        this.agendamentoRepository = agendamentoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProntuarioClinicoDTO.Response> listarPorPet(Long petId) {
        return prontuarioRepository.findByPetIdOrderByDataAtendimentoDesc(petId).stream()
                .map(ProntuarioClinicoDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProntuarioClinicoDTO.Response buscarPorId(Long id) {
        ProntuarioClinico p = obterEntidade(id);
        return ProntuarioClinicoDTO.Response.fromEntity(p);
    }

    @Transactional(readOnly = true)
    public ProntuarioClinico obterEntidade(Long id) {
        return prontuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário Clínico", id));
    }

    @Transactional
    public ProntuarioClinicoDTO.Response registrarAtendimento(ProntuarioClinicoDTO.Request request) {
        Pet pet = petService.obterEntidade(request.petId());
        Funcionario veterinario = funcionarioService.obterEntidade(request.veterinarioId());

        // RN02: Apenas o usuário com perfil de Médico Veterinário pode preencher o prontuário clínico
        if (veterinario.getCargo() != PerfilFuncionario.VETERINARIO) {
            throw new RegraDeNegocioException("Apenas Médicos Veterinários têm permissão para preencher prontuários clínicos médicos (RN02).");
        }

        // RN03: CRMV é obrigatório
        if (veterinario.getCrmv() == null || veterinario.getCrmv().isBlank()) {
            throw new RegraDeNegocioException("O Médico Veterinário responsável deve possuir número de CRMV registrado para emitir prontuário (RN03).");
        }

        ProntuarioClinico prontuario = ProntuarioClinico.builder()
                .pet(pet)
                .veterinario(veterinario)
                .dataAtendimento(LocalDateTime.now())
                .queixaPrincipal(request.queixaPrincipal().trim())
                .sinaisVitais(request.sinaisVitais() != null ? request.sinaisVitais().trim() : null)
                .mucosas(request.mucosas() != null ? request.mucosas().trim() : null)
                .exameFisico(request.exameFisico() != null ? request.exameFisico().trim() : null)
                .hipoteseDiagnostica(request.hipoteseDiagnostica().trim())
                .condutaPrescricao(request.condutaPrescricao().trim())
                .receituarioEmitido(request.receituarioEmitido() != null ? request.receituarioEmitido().trim() : null)
                .build();

        ProntuarioClinico salvo = prontuarioRepository.save(prontuario);

        // Finaliza o atendimento na fila do dia, se houver
        List<Agendamento> agendamentosHoje = agendamentoRepository.findFilaDoDia(LocalDate.now());
        for (Agendamento ag : agendamentosHoje) {
            if (ag.getPet().getId().equals(pet.getId()) && ag.getStatus() != StatusAgendamento.FINALIZADO && ag.getStatus() != StatusAgendamento.CANCELADO) {
                ag.setStatus(StatusAgendamento.FINALIZADO);
                agendamentoRepository.save(ag);
            }
        }

        return ProntuarioClinicoDTO.Response.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public ProntuarioClinicoDTO.ReceitaResponse emitirReceitaOficial(Long prontuarioId) {
        ProntuarioClinico p = obterEntidade(prontuarioId);

        if (p.getVeterinario().getCrmv() == null || p.getVeterinario().getCrmv().isBlank()) {
            throw new RegraDeNegocioException("Não é possível emitir receita médica sem o CRMV do Médico Veterinário (RN03).");
        }

        return new ProntuarioClinicoDTO.ReceitaResponse(
                "VetCare Fauna & Domésticos - Clínica e Consultoria Veterinária",
                p.getVeterinario().getNome(),
                p.getVeterinario().getCrmv(),
                p.getPet().getNome(),
                p.getPet().getEspecieRaca(),
                p.getPet().getTutor().getNome(),
                p.getDataAtendimento(),
                p.getReceituarioEmitido() != null ? p.getReceituarioEmitido() : p.getCondutaPrescricao()
        );
    }
}
