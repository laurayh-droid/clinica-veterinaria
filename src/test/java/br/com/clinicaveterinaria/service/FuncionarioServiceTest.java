package br.com.clinicaveterinaria.service;

import br.com.clinicaveterinaria.dto.FuncionarioDTO;
import br.com.clinicaveterinaria.enums.PerfilFuncionario;
import br.com.clinicaveterinaria.exception.RegraDeNegocioException;
import br.com.clinicaveterinaria.model.Funcionario;
import br.com.clinicaveterinaria.repository.FuncionarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private FuncionarioService funcionarioService;

    @Test
    @DisplayName("RN03 - Deve rejeitar cadastro de Veterinário sem CRMV")
    void deveRejeitarVeterinarioSemCrmv() {
        FuncionarioDTO.Request request = new FuncionarioDTO.Request(
                "Dr. Roberto",
                "123.456.789-00",
                "roberto@vetcare.com",
                "(11) 99999-8888",
                PerfilFuncionario.VETERINARIO,
                "" // CRMV vazio
        );

        when(funcionarioRepository.existsByCpf(any())).thenReturn(false);
        when(funcionarioRepository.existsByEmail(any())).thenReturn(false);

        assertThrows(RegraDeNegocioException.class, () -> funcionarioService.cadastrar(request));
        verify(funcionarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN03 - Deve cadastrar Veterinário com CRMV válido")
    void deveCadastrarVeterinarioComCrmvValido() {
        FuncionarioDTO.Request request = new FuncionarioDTO.Request(
                "Dr. Carlos",
                "111.222.333-44",
                "carlos@vetcare.com",
                "(11) 98888-1111",
                PerfilFuncionario.VETERINARIO,
                "CRMV/SP 12345"
        );

        Funcionario fSalvo = Funcionario.builder()
                .id(1L)
                .nome(request.nome())
                .cpf(request.cpf())
                .email(request.email())
                .telefone(request.telefone())
                .cargo(request.cargo())
                .crmv(request.crmv())
                .ativo(true)
                .build();

        when(funcionarioRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(funcionarioRepository.existsByEmail(request.email())).thenReturn(false);
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(fSalvo);

        FuncionarioDTO.Response response = funcionarioService.cadastrar(request);

        assertNotNull(response);
        assertEquals("CRMV/SP 12345", response.crmv());
        assertEquals(PerfilFuncionario.VETERINARIO, response.cargo());
        verify(funcionarioRepository, times(1)).save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com CPF duplicado")
    void deveRejeitarCpfDuplicado() {
        FuncionarioDTO.Request request = new FuncionarioDTO.Request(
                "Lucas Mendes",
                "555.666.777-88",
                "lucas@vetcare.com",
                "(11) 97777-2222",
                PerfilFuncionario.ESPECIALISTA_SILVESTRES,
                null
        );

        when(funcionarioRepository.existsByCpf(request.cpf())).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> funcionarioService.cadastrar(request));
        verify(funcionarioRepository, never()).save(any());
    }
}
