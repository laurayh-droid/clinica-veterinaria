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

        Agendamento salvo = Agendamento.builder()
                .id(10L)
                .data(LocalDate.now())
                .hora("10:00")
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

        Agendamento salvo = Agendamento.builder()
                .id(11L)
                .data(LocalDate.now())
                .hora("14:00")
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
}
