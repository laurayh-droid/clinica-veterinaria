package br.com.clinicaveterinaria.service;

import br.com.clinicaveterinaria.dto.GuiaManejoSilvestreDTO;
import br.com.clinicaveterinaria.enums.CategoriaPet;
import br.com.clinicaveterinaria.enums.PerfilFuncionario;
import br.com.clinicaveterinaria.exception.RegraDeNegocioException;
import br.com.clinicaveterinaria.model.Funcionario;
import br.com.clinicaveterinaria.model.GuiaManejoSilvestre;
import br.com.clinicaveterinaria.model.Pet;
import br.com.clinicaveterinaria.model.Tutor;
import br.com.clinicaveterinaria.repository.AgendamentoRepository;
import br.com.clinicaveterinaria.repository.GuiaManejoSilvestreRepository;
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
class GuiaManejoSilvestreServiceTest {

    @Mock
    private GuiaManejoSilvestreRepository guiaRepository;

    @Mock
    private PetService petService;

    @Mock
    private FuncionarioService funcionarioService;

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @InjectMocks
    private GuiaManejoSilvestreService guiaManejoSilvestreService;

    @Test
    @DisplayName("RN02 - Deve impedir que recepcionista emita Guia de Manejo Silvestre")
    void deveImpedirRecepcionistaDeEmitirGuiaManejo() {
        Pet pet = Pet.builder().id(101L).nome("Kiko").categoria(CategoriaPet.SILVESTRE).build();
        Funcionario atendente = Funcionario.builder().id(3L).nome("Ana Silva").cargo(PerfilFuncionario.ATENDENTE).build();

        when(petService.obterEntidade(101L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(3L)).thenReturn(atendente);

        GuiaManejoSilvestreDTO.Request request = new GuiaManejoSilvestreDTO.Request(
                101L,
                3L,
                "Queixa de estresse",
                "2x2m", "UVB 5.0", "26C", "Pinus", "Forrageamento", "Target"
        );

        assertThrows(RegraDeNegocioException.class, () -> guiaManejoSilvestreService.registrarSessaoManejo(request));
        verify(guiaRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN04 - Deve exigir parâmetros ambientais em guias de animais silvestres/exóticos")
    void deveExigirParametrosAmbientaisParaSilvestres() {
        Pet pet = Pet.builder().id(101L).nome("Kiko").categoria(CategoriaPet.SILVESTRE).build();
        Funcionario especialista = Funcionario.builder().id(2L).nome("Lucas Mendes").cargo(PerfilFuncionario.ESPECIALISTA_SILVESTRES).build();

        when(petService.obterEntidade(101L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(2L)).thenReturn(especialista);

        // Request sem dimensoesRecinto, sem iluminacao e sem enriquecimento
        GuiaManejoSilvestreDTO.Request request = new GuiaManejoSilvestreDTO.Request(
                101L,
                2L,
                "Arrancando penas",
                "", "", "", "", "", ""
        );

        assertThrows(RegraDeNegocioException.class, () -> guiaManejoSilvestreService.registrarSessaoManejo(request));
        verify(guiaRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN02 e RN04 - Deve registrar guia de manejo com sucesso")
    void deveRegistrarGuiaComSucesso() {
        Tutor tutor = Tutor.builder().id(1L).nome("Mariana").build();
        Pet pet = Pet.builder().id(101L).nome("Kiko").tutor(tutor).categoria(CategoriaPet.SILVESTRE).especieRaca("Papagaio").build();
        Funcionario especialista = Funcionario.builder().id(2L).nome("Lucas Mendes").cargo(PerfilFuncionario.ESPECIALISTA_SILVESTRES).build();

        when(petService.obterEntidade(101L)).thenReturn(pet);
        when(funcionarioService.obterEntidade(2L)).thenReturn(especialista);
        when(agendamentoRepository.findFilaDoDia(any())).thenReturn(Collections.emptyList());

        GuiaManejoSilvestre guiaSalva = GuiaManejoSilvestre.builder()
                .id(1L)
                .pet(pet)
                .especialista(especialista)
                .queixaComportamental("Arrancamento de penas")
                .dimensoesRecinto("2m x 1.5m x 2m")
                .parametrosIluminacao("UVB 5.0")
                .enriquecimentoAmbiental("Forrageamento diário")
                .planoTreinamentoAdestramento("Reforço positivo")
                .build();

        when(guiaRepository.save(any(GuiaManejoSilvestre.class))).thenReturn(guiaSalva);

        GuiaManejoSilvestreDTO.Request request = new GuiaManejoSilvestreDTO.Request(
                101L,
                2L,
                "Arrancamento de penas",
                "2m x 1.5m x 2m", "UVB 5.0", "26°C", "Galhos", "Forrageamento diário", "Reforço positivo"
        );

        GuiaManejoSilvestreDTO.Response response = guiaManejoSilvestreService.registrarSessaoManejo(request);

        assertNotNull(response);
        assertEquals("Arrancamento de penas", response.queixaComportamental());
        assertEquals("Lucas Mendes", response.especialistaNome());
        verify(guiaRepository, times(1)).save(any(GuiaManejoSilvestre.class));
    }
}
