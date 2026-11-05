package br.com.clinicaveterinaria.service;

import br.com.clinicaveterinaria.dto.AgendamentoDTO;
import br.com.clinicaveterinaria.enums.CategoriaPet;
import br.com.clinicaveterinaria.enums.PerfilFuncionario;
import br.com.clinicaveterinaria.enums.StatusAgendamento;
import br.com.clinicaveterinaria.enums.TipoServico;
import br.com.clinicaveterinaria.exception.RegraDeNegocioException;
import br.com.clinicaveterinaria.model.Agendamento;
import br.com.clinicaveterinaria.model.Funcionario;
import br.com.clinicaveterinaria.model.Pet;
import br.com.clinicaveterinaria.model.Tutor;
import br.com.clinicaveterinaria.repository.AgendamentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private PetService petService;

    @Mock
    private FuncionarioService funcionarioService;

    @InjectMocks
    private AgendamentoService agendamentoService;

    @Test
    @DisplayName("RN02 - Deve permitir agendamento de consulta médica com Médico Veterinário")
    void deveAgendarConsultaClinicaComVeterinario() {
        Tutor tutor = Tutor.builder().id(1L).nome("Mariana").build();
        Pet pet = Pet.builder().id(101L).nome("Kiko").tutor(tutor).categoria(CategoriaPet.SILVESTRE).build();
        Funcionario vet = Funcionario.builder()
                .id(1L)
                .nome("Dr. Carlos Eduardo")
                .cargo(PerfilFuncionario.VETERINARIO)
                .crmv("CRMV/SP 12345")
                .build();

        when(petService.obterEntidade(101L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(1L)).thenReturn(vet);
        when(agendamentoRepository.findByDataOrderByHoraAsc(any())).thenReturn(List.of());

        Agendamento salvo = Agendamento.builder()
                .id(10L)
                .data(LocalDate.now())
                .hora("10:00")
                .horaFim("10:15")
                .pet(pet)
                .tutor(tutor)
                .tipoServico(TipoServico.CONSULTA_CLINICA)
                .profissional(vet)
                .status(StatusAgendamento.AGENDADO)
                .build();

        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(salvo);

        AgendamentoDTO.Request request = new AgendamentoDTO.Request(
                LocalDate.now(),
                "10:00",
                101L,
                TipoServico.CONSULTA_CLINICA,
                1L,
                "Rotina"
        );

        AgendamentoDTO.Response response = agendamentoService.agendar(request);

        assertNotNull(response);
        assertEquals(TipoServico.CONSULTA_CLINICA, response.tipoServico());
        assertEquals("Dr. Carlos Eduardo", response.profissionalNome());
        verify(agendamentoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("RN02 - Deve impedir agendamento de consulta médica com Adestrador / Especialista em Silvestres")
    void deveImpedirConsultaClinicaComAdestrador() {
        Pet pet = Pet.builder().id(101L).nome("Kiko").build();
        Funcionario adestrador = Funcionario.builder()
                .id(2L)
                .nome("Lucas Mendes")
                .cargo(PerfilFuncionario.ESPECIALISTA_SILVESTRES)
                .build();

        when(petService.obterEntidade(101L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(2L)).thenReturn(adestrador);

        AgendamentoDTO.Request request = new AgendamentoDTO.Request(
                LocalDate.now(),
                "10:00",
                101L,
                TipoServico.CONSULTA_CLINICA,
                2L,
                "Tentativa incoerente"
        );

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class, () -> agendamentoService.agendar(request));
        assertTrue(ex.getMessage().contains("Médico Veterinário"));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN02 - Deve permitir agendamento de consultoria de manejo com Adestrador / Especialista em Silvestres")
    void deveAgendarManejoComEspecialista() {
        Tutor tutor = Tutor.builder().id(1L).nome("Mariana").build();
        Pet pet = Pet.builder().id(101L).nome("Kiko").tutor(tutor).categoria(CategoriaPet.SILVESTRE).build();
        Funcionario adestrador = Funcionario.builder()
                .id(2L)
                .nome("Lucas Mendes")
                .cargo(PerfilFuncionario.ESPECIALISTA_SILVESTRES)
                .build();

        when(petService.obterEntidade(101L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(2L)).thenReturn(adestrador);
        when(agendamentoRepository.findByDataOrderByHoraAsc(any())).thenReturn(List.of());

        Agendamento salvo = Agendamento.builder()
                .id(11L)
                .data(LocalDate.now())
                .hora("14:00")
                .horaFim("14:15")
                .pet(pet)
                .tutor(tutor)
                .tipoServico(TipoServico.CONSULTORIA_MANEJO)
                .profissional(adestrador)
                .status(StatusAgendamento.AGENDADO)
                .build();

        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(salvo);

        AgendamentoDTO.Request request = new AgendamentoDTO.Request(
                LocalDate.now(),
                "14:00",
                101L,
                TipoServico.CONSULTORIA_MANEJO,
                2L,
                "Adequação de viveiro"
        );

        AgendamentoDTO.Response response = agendamentoService.agendar(request);

        assertNotNull(response);
        assertEquals(TipoServico.CONSULTORIA_MANEJO, response.tipoServico());
        assertEquals("Lucas Mendes", response.profissionalNome());
        verify(agendamentoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("RN02 - Deve impedir agendamento de consultoria de manejo com Veterinário")
    void deveImpedirManejoComVeterinario() {
        Pet pet = Pet.builder().id(101L).nome("Kiko").build();
        Funcionario vet = Funcionario.builder()
                .id(1L)
                .nome("Dr. Carlos Eduardo")
                .cargo(PerfilFuncionario.VETERINARIO)
                .build();

        when(petService.obterEntidade(101L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(1L)).thenReturn(vet);

        AgendamentoDTO.Request request = new AgendamentoDTO.Request(
                LocalDate.now(),
                "14:00",
                101L,
                TipoServico.CONSULTORIA_MANEJO,
                1L,
                "Manejo incoerente"
        );

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class, () -> agendamentoService.agendar(request));
        assertTrue(ex.getMessage().contains("Especialista em Silvestres"));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN02 - Deve impedir qualquer agendamento com Recepcionista / Atendente")
    void deveImpedirAgendamentoComAtendente() {
        Pet pet = Pet.builder().id(101L).nome("Kiko").build();
        Funcionario atendente = Funcionario.builder()
                .id(3L)
                .nome("Ana Silva")
                .cargo(PerfilFuncionario.ATENDENTE)
                .build();

        when(petService.obterEntidade(101L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(3L)).thenReturn(atendente);

        AgendamentoDTO.Request request = new AgendamentoDTO.Request(
                LocalDate.now(),
                "15:00",
                101L,
                TipoServico.CONSULTA_CLINICA,
                3L,
                "Tentativa com atendente"
        );

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class, () -> agendamentoService.agendar(request));
        assertTrue(ex.getMessage().contains("Recepcionistas/Atendentes não prestam atendimento"));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN07 - Deve impedir agendamento de consulta no mesmo horário para o mesmo paciente")
    void deveImpedirConsultasSobrepostasMesmoPaciente() {
        LocalDate data = LocalDate.now();
        Tutor tutor = Tutor.builder().id(1L).nome("Mariana").build();
        Pet pet = Pet.builder().id(101L).nome("Kiko").tutor(tutor).categoria(CategoriaPet.SILVESTRE).build();
        Funcionario vet1 = Funcionario.builder().id(1L).nome("Dr. Carlos").cargo(PerfilFuncionario.VETERINARIO).build();
        Funcionario vet2 = Funcionario.builder().id(4L).nome("Dra. Paula").cargo(PerfilFuncionario.VETERINARIO).build();

        Agendamento existente = Agendamento.builder()
                .id(1L)
                .data(data)
                .hora("10:00")
                .horaFim("10:15")
                .pet(pet)
                .profissional(vet1)
                .status(StatusAgendamento.AGENDADO)
                .build();

        when(petService.obterEntidade(101L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(4L)).thenReturn(vet2);
        when(agendamentoRepository.findByDataOrderByHoraAsc(data)).thenReturn(List.of(existente));

        AgendamentoDTO.Request request = new AgendamentoDTO.Request(
                data,
                "10:00",
                101L,
                TipoServico.CONSULTA_CLINICA,
                4L,
                "Segunda consulta no mesmo horário"
        );

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class, () -> agendamentoService.agendar(request));
        assertTrue(ex.getMessage().contains("Não é permitido marcar consultas no mesmo horário para o mesmo paciente"));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN07 - Deve impedir agendamento com sobreposição durante o intervalo de 15 minutos para o mesmo paciente")
    void deveImpedirConsultasSobrepostasMesmoPacienteDentroDos15Minutos() {
        LocalDate data = LocalDate.now();
        Tutor tutor = Tutor.builder().id(1L).nome("Mariana").build();
        Pet pet = Pet.builder().id(101L).nome("Kiko").tutor(tutor).categoria(CategoriaPet.SILVESTRE).build();
        Funcionario vet = Funcionario.builder().id(1L).nome("Dr. Carlos").cargo(PerfilFuncionario.VETERINARIO).build();

        Agendamento existente = Agendamento.builder()
                .id(1L)
                .data(data)
                .hora("10:00")
                .horaFim("10:15")
                .pet(pet)
                .profissional(vet)
                .status(StatusAgendamento.AGENDADO)
                .build();

        when(petService.obterEntidade(101L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(1L)).thenReturn(vet);
        when(agendamentoRepository.findByDataOrderByHoraAsc(data)).thenReturn(List.of(existente));

        // Tenta marcar às 10:10 (sobrepõe com 10:00 - 10:15)
        AgendamentoDTO.Request request = new AgendamentoDTO.Request(
                data,
                "10:10",
                101L,
                TipoServico.CONSULTA_CLINICA,
                1L,
                "Sobreposição parcial"
        );

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class, () -> agendamentoService.agendar(request));
        assertTrue(ex.getMessage().contains("RN07"));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN07 - Deve permitir agendamento após término dos 15 minutos para o mesmo paciente")
    void devePermitirConsultaApos15MinutosMesmoPaciente() {
        LocalDate data = LocalDate.now();
        Tutor tutor = Tutor.builder().id(1L).nome("Mariana").build();
        Pet pet = Pet.builder().id(101L).nome("Kiko").tutor(tutor).categoria(CategoriaPet.SILVESTRE).build();
        Funcionario vet = Funcionario.builder().id(1L).nome("Dr. Carlos").cargo(PerfilFuncionario.VETERINARIO).build();

        Agendamento existente = Agendamento.builder()
                .id(1L)
                .data(data)
                .hora("10:00")
                .horaFim("10:15")
                .pet(pet)
                .profissional(vet)
                .status(StatusAgendamento.AGENDADO)
                .build();

        when(petService.obterEntidade(101L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(1L)).thenReturn(vet);
        when(agendamentoRepository.findByDataOrderByHoraAsc(data)).thenReturn(List.of(existente));

        Agendamento salvo = Agendamento.builder()
                .id(2L)
                .data(data)
                .hora("10:15")
                .horaFim("10:30")
                .pet(pet)
                .tutor(tutor)
                .tipoServico(TipoServico.RETORNO)
                .profissional(vet)
                .status(StatusAgendamento.AGENDADO)
                .build();

        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(salvo);

        // Agendamento às 10:15 é perfeitamente válido pois 10:00-10:15 já terminou
        AgendamentoDTO.Request request = new AgendamentoDTO.Request(
                data,
                "10:15",
                101L,
                TipoServico.RETORNO,
                1L,
                "Retorno após consulta"
        );

        AgendamentoDTO.Response response = agendamentoService.agendar(request);

        assertNotNull(response);
        assertEquals("10:15", response.hora());
        assertEquals("10:30", response.horaFim());
        verify(agendamentoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("RN07 - Deve impedir agendamento no mesmo horário para o mesmo profissional")
    void deveImpedirConsultasSobrepostasMesmoProfissional() {
        LocalDate data = LocalDate.now();
        Tutor tutor1 = Tutor.builder().id(1L).nome("Mariana").build();
        Pet pet1 = Pet.builder().id(101L).nome("Kiko").tutor(tutor1).categoria(CategoriaPet.SILVESTRE).build();

        Tutor tutor2 = Tutor.builder().id(2L).nome("Roberto").build();
        Pet pet2 = Pet.builder().id(102L).nome("Tufão").tutor(tutor2).categoria(CategoriaPet.SILVESTRE).build();

        Funcionario vet = Funcionario.builder().id(1L).nome("Dr. Carlos").cargo(PerfilFuncionario.VETERINARIO).build();

        Agendamento existente = Agendamento.builder()
                .id(1L)
                .data(data)
                .hora("10:00")
                .horaFim("10:15")
                .pet(pet1)
                .profissional(vet)
                .status(StatusAgendamento.AGENDADO)
                .build();

        when(petService.obterEntidade(102L)).thenReturn(pet2);
        when(funcionarioService.obterEntidade(1L)).thenReturn(vet);
        when(agendamentoRepository.findByDataOrderByHoraAsc(data)).thenReturn(List.of(existente));

        // Tenta agendar outro pet com o mesmo profissional no mesmo horário
        AgendamentoDTO.Request request = new AgendamentoDTO.Request(
                data,
                "10:00",
                102L,
                TipoServico.CONSULTA_CLINICA,
                1L,
                "Consulta de outro pet com mesmo veterinário"
        );

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class, () -> agendamentoService.agendar(request));
        assertTrue(ex.getMessage().contains("O profissional 'Dr. Carlos' já possui atendimento agendado"));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN07 - Deve permitir agendamento no mesmo horário se consulta anterior estiver cancelada")
    void devePermitirAgendamentoSeConsultaAnteriorEstiverCancelada() {
        LocalDate data = LocalDate.now();
        Tutor tutor = Tutor.builder().id(1L).nome("Mariana").build();
        Pet pet = Pet.builder().id(101L).nome("Kiko").tutor(tutor).categoria(CategoriaPet.SILVESTRE).build();
        Funcionario vet = Funcionario.builder().id(1L).nome("Dr. Carlos").cargo(PerfilFuncionario.VETERINARIO).build();

        Agendamento cancelado = Agendamento.builder()
                .id(1L)
                .data(data)
                .hora("10:00")
                .horaFim("10:15")
                .pet(pet)
                .profissional(vet)
                .status(StatusAgendamento.CANCELADO)
                .build();

        when(petService.obterEntidade(101L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(1L)).thenReturn(vet);
        when(agendamentoRepository.findByDataOrderByHoraAsc(data)).thenReturn(List.of(cancelado));

        Agendamento novo = Agendamento.builder()
                .id(2L)
                .data(data)
                .hora("10:00")
                .horaFim("10:15")
                .pet(pet)
                .tutor(tutor)
                .tipoServico(TipoServico.CONSULTA_CLINICA)
                .profissional(vet)
                .status(StatusAgendamento.AGENDADO)
                .build();

        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(novo);

        AgendamentoDTO.Request request = new AgendamentoDTO.Request(
                data,
                "10:00",
                101L,
                TipoServico.CONSULTA_CLINICA,
                1L,
                "Reagendamento após cancelamento"
        );

        AgendamentoDTO.Response response = agendamentoService.agendar(request);

        assertNotNull(response);
        assertEquals(StatusAgendamento.AGENDADO, response.status());
        verify(agendamentoRepository, times(1)).save(any());
    }
}
