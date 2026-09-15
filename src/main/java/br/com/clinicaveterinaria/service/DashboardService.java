package br.com.clinicaveterinaria.service;

import br.com.clinicaveterinaria.dto.DashboardMetricasDTO;
import br.com.clinicaveterinaria.enums.CategoriaPet;
import br.com.clinicaveterinaria.enums.StatusAgendamento;
import br.com.clinicaveterinaria.repository.AgendamentoRepository;
import br.com.clinicaveterinaria.repository.FuncionarioRepository;
import br.com.clinicaveterinaria.repository.PetRepository;
import br.com.clinicaveterinaria.repository.TutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DashboardService {

    private final AgendamentoRepository agendamentoRepository;
    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;
    private final FuncionarioRepository funcionarioRepository;

    public DashboardService(AgendamentoRepository agendamentoRepository,
                            PetRepository petRepository,
                            TutorRepository tutorRepository,
                            FuncionarioRepository funcionarioRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Transactional(readOnly = true)
    public DashboardMetricasDTO obterMetricasHoje() {
        LocalDate hoje = LocalDate.now();

        long totalConsultasHoje = agendamentoRepository.countByData(hoje);
        long totalAguardandoFila = agendamentoRepository.countByDataAndStatus(hoje, StatusAgendamento.AGUARDANDO);
        long totalEmAtendimento = agendamentoRepository.countByDataAndStatus(hoje, StatusAgendamento.EM_ATENDIMENTO);
        long totalFinalizadosHoje = agendamentoRepository.countByDataAndStatus(hoje, StatusAgendamento.FINALIZADO);

        long silvestres = petRepository.countByCategoria(CategoriaPet.SILVESTRE);
        long exoticos = petRepository.countByCategoria(CategoriaPet.EXOTICO);
        long domesticos = petRepository.countByCategoria(CategoriaPet.DOMESTICO);

        long totalTutores = tutorRepository.count();
        long totalProfissionaisAtivos = funcionarioRepository.findByAtivoTrue().size();

        return new DashboardMetricasDTO(
                hoje,
                totalConsultasHoje,
                totalAguardandoFila,
                totalEmAtendimento,
                totalFinalizadosHoje,
                silvestres + exoticos,
                domesticos,
                totalTutores,
                totalProfissionaisAtivos
        );
    }
}
