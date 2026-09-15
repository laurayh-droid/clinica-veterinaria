package br.com.clinicaveterinaria.service;

import br.com.clinicaveterinaria.dto.AgendamentoDTO;
import br.com.clinicaveterinaria.enums.PerfilFuncionario;
import br.com.clinicaveterinaria.enums.StatusAgendamento;
import br.com.clinicaveterinaria.enums.TipoServico;
import br.com.clinicaveterinaria.exception.RegraDeNegocioException;
import br.com.clinicaveterinaria.exception.ResourceNotFoundException;
import br.com.clinicaveterinaria.model.Agendamento;
import br.com.clinicaveterinaria.model.Funcionario;
import br.com.clinicaveterinaria.model.Pet;
import br.com.clinicaveterinaria.repository.AgendamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final PetService petService;
    private final FuncionarioService funcionarioService;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              PetService petService,
                              FuncionarioService funcionarioService) {
        this.agendamentoRepository = agendamentoRepository;
        this.petService = petService;
        this.funcionarioService = funcionarioService;
    }

    @Transactional(readOnly = true)
    public List<AgendamentoDTO.Response> listarPorData(LocalDate data) {
        LocalDate dataConsulta = (data != null) ? data : LocalDate.now();
        return agendamentoRepository.findFilaDoDia(dataConsulta).stream()
                .map(AgendamentoDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AgendamentoDTO.Response> listarFilaHoje() {
        return listarPorData(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<AgendamentoDTO.Response> listarPorPet(Long petId) {
        return agendamentoRepository.findByPetIdOrderByDataDescHoraDesc(petId).stream()
                .map(AgendamentoDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AgendamentoDTO.Response buscarPorId(Long id) {
        Agendamento ag = obterEntidade(id);
        return AgendamentoDTO.Response.fromEntity(ag);
    }

    @Transactional(readOnly = true)
    public Agendamento obterEntidade(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento", id));
    }

    @Transactional
    public AgendamentoDTO.Response agendar(AgendamentoDTO.Request request) {
        Pet pet = petService.obterEntidade(request.petId());

        Funcionario profissional = null;
        if (request.profissionalId() != null) {
            profissional = funcionarioService.obterEntidade(request.profissionalId());
            validarCompetenciaProfissional(request.tipoServico(), profissional);
        }

        Agendamento agendamento = Agendamento.builder()
                .data(request.data())
                .hora(request.hora().trim())
                .pet(pet)
                .tutor(pet.getTutor())
                .tipoServico(request.tipoServico())
                .profissional(profissional)
                .status(StatusAgendamento.AGENDADO)
                .observacoes(request.observacoes())
                .build();

        Agendamento salvo = agendamentoRepository.save(agendamento);
        return AgendamentoDTO.Response.fromEntity(salvo);
    }

    @Transactional
    public AgendamentoDTO.Response atualizarStatus(Long id, StatusAgendamento novoStatus) {
        Agendamento agendamento = obterEntidade(id);
        agendamento.setStatus(novoStatus);
        Agendamento salvo = agendamentoRepository.save(agendamento);
        return AgendamentoDTO.Response.fromEntity(salvo);
    }

    @Transactional
    public void cancelar(Long id) {
        atualizarStatus(id, StatusAgendamento.CANCELADO);
    }

    @Transactional
    public void remover(Long id) {
        Agendamento agendamento = obterEntidade(id);
        agendamentoRepository.delete(agendamento);
    }

    private void validarCompetenciaProfissional(TipoServico tipoServico, Funcionario profissional) {
        if (profissional == null) return;

        if (tipoServico == TipoServico.CONSULTA_CLINICA || tipoServico == TipoServico.VACINACAO) {
            if (profissional.getCargo() != PerfilFuncionario.VETERINARIO) {
                throw new RegraDeNegocioException("Consultas clínicas e vacinações devem ser atribuídas a um Médico Veterinário (RN02).");
            }
        } else if (tipoServico == TipoServico.CONSULTORIA_MANEJO || tipoServico == TipoServico.CHECKUP_NUTRICIONAL) {
            if (profissional.getCargo() != PerfilFuncionario.ESPECIALISTA_SILVESTRES && profissional.getCargo() != PerfilFuncionario.ADMINISTRADOR) {
                throw new RegraDeNegocioException("Consultorias de manejo silvestre e adestramento devem ser atribuídas a um Especialista em Silvestres / Adestrador (RN02).");
            }
        }
    }
}
