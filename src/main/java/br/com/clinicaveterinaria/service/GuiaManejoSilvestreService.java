package br.com.clinicaveterinaria.service;

import br.com.clinicaveterinaria.dto.GuiaManejoSilvestreDTO;
import br.com.clinicaveterinaria.enums.CategoriaPet;
import br.com.clinicaveterinaria.enums.PerfilFuncionario;
import br.com.clinicaveterinaria.enums.StatusAgendamento;
import br.com.clinicaveterinaria.exception.RegraDeNegocioException;
import br.com.clinicaveterinaria.exception.ResourceNotFoundException;
import br.com.clinicaveterinaria.model.Agendamento;
import br.com.clinicaveterinaria.model.Funcionario;
import br.com.clinicaveterinaria.model.GuiaManejoSilvestre;
import br.com.clinicaveterinaria.model.Pet;
import br.com.clinicaveterinaria.repository.AgendamentoRepository;
import br.com.clinicaveterinaria.repository.GuiaManejoSilvestreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GuiaManejoSilvestreService {

    private final GuiaManejoSilvestreRepository guiaRepository;
    private final PetService petService;
    private final FuncionarioService funcionarioService;
    private final AgendamentoRepository agendamentoRepository;

    public GuiaManejoSilvestreService(GuiaManejoSilvestreRepository guiaRepository,
                                      PetService petService,
                                      FuncionarioService funcionarioService,
                                      AgendamentoRepository agendamentoRepository) {
        this.guiaRepository = guiaRepository;
        this.petService = petService;
        this.funcionarioService = funcionarioService;
        this.agendamentoRepository = agendamentoRepository;
    }

    @Transactional(readOnly = true)
    public List<GuiaManejoSilvestreDTO.Response> listarPorPet(Long petId) {
        return guiaRepository.findByPetIdOrderByDataAtendimentoDesc(petId).stream()
                .map(GuiaManejoSilvestreDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public GuiaManejoSilvestreDTO.Response buscarPorId(Long id) {
        GuiaManejoSilvestre g = obterEntidade(id);
        return GuiaManejoSilvestreDTO.Response.fromEntity(g);
    }

    @Transactional(readOnly = true)
    public GuiaManejoSilvestre obterEntidade(Long id) {
        return guiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guia de Manejo Silvestre", id));
    }

    @Transactional
    public GuiaManejoSilvestreDTO.Response registrarSessaoManejo(GuiaManejoSilvestreDTO.Request request) {
        Pet pet = petService.obterEntidade(request.petId());
        Funcionario especialista = funcionarioService.obterEntidade(request.especialistaId());

        // RN02: Apenas Adestrador / Especialista em Silvestres (ou Administrador) pode emitir Guia de Manejo Silvestre
        if (especialista.getCargo() != PerfilFuncionario.ESPECIALISTA_SILVESTRES && especialista.getCargo() != PerfilFuncionario.ADMINISTRADOR) {
            throw new RegraDeNegocioException("Apenas Especialistas em Silvestres e Adestradores têm competência para emitir Guias de Manejo (RN02).");
        }

        // RN04: Parâmetros do Guia de Manejo para fauna silvestre/exótica
        if (pet.getCategoria() == CategoriaPet.SILVESTRE || pet.getCategoria() == CategoriaPet.EXOTICO) {
            if ((request.dimensoesRecinto() == null || request.dimensoesRecinto().isBlank()) &&
                (request.parametrosIluminacao() == null || request.parametrosIluminacao().isBlank()) &&
                (request.enriquecimentoAmbiental() == null || request.enriquecimentoAmbiental().isBlank())) {
                throw new RegraDeNegocioException("Para pets silvestres e exóticos, é obrigatório registrar diretrizes de recinto, iluminação/UVB ou enriquecimento ambiental (RN04).");
            }
        }

        GuiaManejoSilvestre guia = GuiaManejoSilvestre.builder()
                .pet(pet)
                .especialista(especialista)
                .dataAtendimento(LocalDateTime.now())
                .queixaComportamental(request.queixaComportamental().trim())
                .dimensoesRecinto(request.dimensoesRecinto() != null ? request.dimensoesRecinto().trim() : null)
                .parametrosIluminacao(request.parametrosIluminacao() != null ? request.parametrosIluminacao().trim() : null)
                .parametrosTemperatura(request.parametrosTemperatura() != null ? request.parametrosTemperatura().trim() : null)
                .tipoSubstrato(request.tipoSubstrato() != null ? request.tipoSubstrato().trim() : null)
                .enriquecimentoAmbiental(request.enriquecimentoAmbiental() != null ? request.enriquecimentoAmbiental().trim() : null)
                .planoTreinamentoAdestramento(request.planoTreinamentoAdestramento() != null ? request.planoTreinamentoAdestramento().trim() : null)
                .build();

        GuiaManejoSilvestre salvo = guiaRepository.save(guia);

        // Finaliza o atendimento na fila do dia, se houver
        List<Agendamento> agendamentosHoje = agendamentoRepository.findFilaDoDia(LocalDate.now());
        for (Agendamento ag : agendamentosHoje) {
            if (ag.getPet().getId().equals(pet.getId()) && ag.getStatus() != StatusAgendamento.FINALIZADO && ag.getStatus() != StatusAgendamento.CANCELADO) {
                ag.setStatus(StatusAgendamento.FINALIZADO);
                agendamentoRepository.save(ag);
            }
        }

        return GuiaManejoSilvestreDTO.Response.fromEntity(salvo);
    }
}
