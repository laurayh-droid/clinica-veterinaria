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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AgendamentoService {

    public static final int DURACAO_PADRAO_MINUTOS = 15;

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
        }
        validarCompetenciaProfissional(request.tipoServico(), profissional);

        String horaInicio = formatarHora(request.hora());
        String horaFim = (request.horaFim() != null && !request.horaFim().isBlank())
                ? formatarHora(request.horaFim())
                : Agendamento.calcularHoraFim(horaInicio, DURACAO_PADRAO_MINUTOS);

        // Validação RN07: Duração padrão de 15 minutos e bloqueio de consultas no mesmo horário para o mesmo paciente ou profissional
        validarConflitoHorarios(request.data(), horaInicio, horaFim, pet, profissional, null);

        Agendamento agendamento = Agendamento.builder()
                .data(request.data())
                .hora(horaInicio)
                .horaFim(horaFim)
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

    /**
     * Valida conflito de horários (RN07).
     * Cada atendimento tem duração padrão de 15 minutos.
     * Não é permitido agendar no mesmo horário para o mesmo paciente ou com o mesmo profissional.
     */
    public void validarConflitoHorarios(LocalDate data, String horaInicioStr, String horaFimStr, Pet pet, Funcionario profissional, Long agendamentoIdIgnorado) {
        LocalTime novoInicio = LocalTime.parse(horaInicioStr, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime novoFim = LocalTime.parse(horaFimStr, DateTimeFormatter.ofPattern("HH:mm"));

        if (!novoFim.isAfter(novoInicio)) {
            throw new RegraDeNegocioException("O horário de término da consulta deve ser posterior ao horário de início.");
        }

        List<Agendamento> agendamentosDoDia = agendamentoRepository.findByDataOrderByHoraAsc(data);

        for (Agendamento existente : agendamentosDoDia) {
            if (agendamentoIdIgnorado != null && agendamentoIdIgnorado.equals(existente.getId())) {
                continue;
            }
            if (existente.getStatus() == StatusAgendamento.CANCELADO) {
                continue;
            }

            LocalTime exInicio = LocalTime.parse(formatarHora(existente.getHora()), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime exFim = (existente.getHoraFim() != null && !existente.getHoraFim().isBlank())
                    ? LocalTime.parse(formatarHora(existente.getHoraFim()), DateTimeFormatter.ofPattern("HH:mm"))
                    : exInicio.plusMinutes(DURACAO_PADRAO_MINUTOS);

            // Verifica sobreposição de intervalos: [novoInicio, novoFim) e [exInicio, exFim)
            boolean sobreposicao = novoInicio.isBefore(exFim) && exInicio.isBefore(novoFim);

            if (sobreposicao) {
                if (existente.getPet() != null && pet != null && existente.getPet().getId().equals(pet.getId())) {
                    throw new RegraDeNegocioException(String.format(
                            "Conflito de agendamento: Não é permitido marcar consultas no mesmo horário para o mesmo paciente. O pet '%s' já possui atendimento agendado no dia %s das %s às %s. A duração padrão da consulta é de %d minutos; novo agendamento só é permitido a partir das %s (RN07).",
                            pet.getNome(),
                            data,
                            exInicio.format(DateTimeFormatter.ofPattern("HH:mm")),
                            exFim.format(DateTimeFormatter.ofPattern("HH:mm")),
                            DURACAO_PADRAO_MINUTOS,
                            exFim.format(DateTimeFormatter.ofPattern("HH:mm"))
                    ));
                }

                if (existente.getProfissional() != null && profissional != null && existente.getProfissional().getId().equals(profissional.getId())) {
                    throw new RegraDeNegocioException(String.format(
                            "Conflito de agendamento: O profissional '%s' já possui atendimento agendado no dia %s das %s às %s (duração padrão de %d minutos). Escolha outro horário disponível (RN07).",
                            profissional.getNome(),
                            data,
                            exInicio.format(DateTimeFormatter.ofPattern("HH:mm")),
                            exFim.format(DateTimeFormatter.ofPattern("HH:mm")),
                            DURACAO_PADRAO_MINUTOS
                    ));
                }
            }
        }
    }

    private String formatarHora(String hora) {
        if (hora == null || hora.isBlank()) {
            throw new RegraDeNegocioException("O horário do agendamento é obrigatório.");
        }
        String limpa = hora.trim();
        if (limpa.length() == 4 && limpa.charAt(1) == ':') {
            limpa = "0" + limpa;
        } else if (limpa.length() > 5) {
            limpa = limpa.substring(0, 5);
        }
        return limpa;
    }

    private void validarCompetenciaProfissional(TipoServico tipoServico, Funcionario profissional) {
        if (profissional == null) {
            throw new RegraDeNegocioException("O profissional responsável é obrigatório para realizar o agendamento.");
        }

        if (profissional.getCargo() == PerfilFuncionario.ATENDENTE) {
            throw new RegraDeNegocioException("Recepcionistas/Atendentes não prestam atendimento técnico ou clínico e não podem ser designados como profissional responsável (RN02).");
        }

        if (tipoServico == TipoServico.CONSULTA_CLINICA || tipoServico == TipoServico.VACINACAO || tipoServico == TipoServico.RETORNO) {
            if (profissional.getCargo() != PerfilFuncionario.VETERINARIO) {
                throw new RegraDeNegocioException("Atendimentos clínicos (consultas, vacinações e retornos) devem ser atribuídos a um Médico Veterinário (RN02).");
            }
        } else if (tipoServico == TipoServico.CONSULTORIA_MANEJO || tipoServico == TipoServico.CHECKUP_NUTRICIONAL) {
            if (profissional.getCargo() != PerfilFuncionario.ESPECIALISTA_SILVESTRES && profissional.getCargo() != PerfilFuncionario.ADMINISTRADOR) {
                throw new RegraDeNegocioException("Consultorias de manejo silvestre, recinto e adestramento devem ser atribuídas a um Especialista em Silvestres / Adestrador (RN02).");
            }
        }
    }
}
