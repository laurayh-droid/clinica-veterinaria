package br.com.clinicaveterinaria.service;

import br.com.clinicaveterinaria.dto.ProntuarioClinicoDTO;
import br.com.clinicaveterinaria.enums.CategoriaPet;
import br.com.clinicaveterinaria.enums.PerfilFuncionario;
import br.com.clinicaveterinaria.exception.RegraDeNegocioException;
import br.com.clinicaveterinaria.model.Funcionario;
import br.com.clinicaveterinaria.model.Pet;
import br.com.clinicaveterinaria.model.ProntuarioClinico;
import br.com.clinicaveterinaria.model.Tutor;
import br.com.clinicaveterinaria.repository.AgendamentoRepository;
import br.com.clinicaveterinaria.repository.ProntuarioClinicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProntuarioClinicoServiceTest {

    @Mock
    private ProntuarioClinicoRepository prontuarioRepository;

    @Mock
    private PetService petService;

    @Mock
    private FuncionarioService funcionarioService;

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @InjectMocks
    private ProntuarioClinicoService prontuarioService;

    @Test
    @DisplayName("RN02 - Deve impedir que não-veterinários registrem prontuários clínicos")
    void deveImpedirNaoVeterinarioDeRegistrarProntuario() {
        Pet pet = Pet.builder().id(101L).nome("Kiko").categoria(CategoriaPet.SILVESTRE).especieRaca("Papagaio").build();
        Funcionario adestrador = Funcionario.builder()
                .id(2L)
                .nome("Lucas Mendes")
                .cargo(PerfilFuncionario.ESPECIALISTA_SILVESTRES)
                .crmv(null)
                .build();

        when(petService.obterEntidade(101L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(2L)).thenReturn(adestrador);

        ProntuarioClinicoDTO.Request request = new ProntuarioClinicoDTO.Request(
                101L,
                2L,
                "Exame geral",
                null, null, null,
                "Hígido",
                "Sem medicação",
                null
        );

        assertThrows(RegraDeNegocioException.class, () -> prontuarioService.registrarAtendimento(request));
        verify(prontuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN02 e RN03 - Deve registrar prontuário quando executado por Médico Veterinário com CRMV")
    void deveRegistrarProntuarioComVeterinarioComCrmv() {
        Tutor tutor = Tutor.builder().id(1L).nome("Mariana").build();
        Pet pet = Pet.builder().id(102L).nome("Tufão").tutor(tutor).categoria(CategoriaPet.SILVESTRE).especieRaca("Jabuti").build();
        Funcionario vet = Funcionario.builder()
                .id(1L)
                .nome("Dr. Carlos Eduardo")
                .cargo(PerfilFuncionario.VETERINARIO)
                .crmv("CRMV/SP 12345")
                .build();

        when(petService.obterEntidade(102L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(1L)).thenReturn(vet);
        when(agendamentoRepository.findFilaDoDia(any())).thenReturn(Collections.emptyList());

        ProntuarioClinico salvo = ProntuarioClinico.builder()
                .id(1L)
                .pet(pet)
                .veterinario(vet)
                .queixaPrincipal("Check-up de carapaça")
                .hipoteseDiagnostica("Hígido")
                .condutaPrescricao("Cálcio com D3")
                .build();

        when(prontuarioRepository.save(any(ProntuarioClinico.class))).thenReturn(salvo);

        ProntuarioClinicoDTO.Request request = new ProntuarioClinicoDTO.Request(
                102L,
                1L,
                "Check-up de carapaça",
                "FC: 45", "Rósea", "Sem alterações",
                "Hígido",
                "Cálcio com D3",
                "Cálcio D3 pó"
        );

        ProntuarioClinicoDTO.Response response = prontuarioService.registrarAtendimento(request);

        assertNotNull(response);
        assertEquals("Check-up de carapaça", response.queixaPrincipal());
        assertEquals("Dr. Carlos Eduardo", response.veterinarioNome());
        verify(prontuarioRepository, times(1)).save(any(ProntuarioClinico.class));
    }
}
