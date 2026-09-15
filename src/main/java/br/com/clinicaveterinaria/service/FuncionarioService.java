package br.com.clinicaveterinaria.service;

import br.com.clinicaveterinaria.dto.FuncionarioDTO;
import br.com.clinicaveterinaria.enums.PerfilFuncionario;
import br.com.clinicaveterinaria.exception.RegraDeNegocioException;
import br.com.clinicaveterinaria.exception.ResourceNotFoundException;
import br.com.clinicaveterinaria.model.Funcionario;
import br.com.clinicaveterinaria.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @Transactional(readOnly = true)
    public List<FuncionarioDTO.Response> listarTodos() {
        return funcionarioRepository.findAll().stream()
                .map(FuncionarioDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FuncionarioDTO.Response> listarAtivos() {
        return funcionarioRepository.findByAtivoTrue().stream()
                .map(FuncionarioDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FuncionarioDTO.Response> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }
        return funcionarioRepository.findByNomeContainingIgnoreCaseOrCpfContaining(termo, termo).stream()
                .map(FuncionarioDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public FuncionarioDTO.Response buscarPorId(Long id) {
        Funcionario f = obterEntidade(id);
        return FuncionarioDTO.Response::fromEntity.apply(f);
    }

    @Transactional(readOnly = true)
    public Funcionario obterEntidade(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário", id));
    }

    @Transactional
    public FuncionarioDTO.Response cadastrar(FuncionarioDTO.Request request) {
        // Validações de Unicidade
        if (funcionarioRepository.existsByCpf(request.cpf())) {
            throw new RegraDeNegocioException("Já existe um funcionário cadastrado com o CPF " + request.cpf());
        }
        if (funcionarioRepository.existsByEmail(request.email())) {
            throw new RegraDeNegocioException("Já existe um funcionário cadastrado com o e-mail " + request.email());
        }

        // RN03: Validação de CRMV para Médicos Veterinários
        validarCrmv(request.cargo(), request.crmv());

        Funcionario funcionario = Funcionario.builder()
                .nome(request.nome().trim())
                .cpf(request.cpf().trim())
                .email(request.email().trim().toLowerCase())
                .telefone(request.telefone().trim())
                .cargo(request.cargo())
                .crmv(request.cargo() == PerfilFuncionario.VETERINARIO ? request.crmv().trim() : null)
                .ativo(true)
                .build();

        Funcionario salvo = funcionarioRepository.save(funcionario);
        return FuncionarioDTO.Response.fromEntity(salvo);
    }

    @Transactional
    public FuncionarioDTO.Response atualizar(Long id, FuncionarioDTO.Request request) {
        Funcionario funcionario = obterEntidade(id);

        // Se CPF mudou, verificar unicidade
        if (!funcionario.getCpf().equals(request.cpf()) && funcionarioRepository.existsByCpf(request.cpf())) {
            throw new RegraDeNegocioException("O CPF informado já está em uso por outro profissional.");
        }
        // Se e-mail mudou, verificar unicidade
        if (!funcionario.getEmail().equalsIgnoreCase(request.email()) && funcionarioRepository.existsByEmail(request.email())) {
            throw new RegraDeNegocioException("O e-mail informado já está em uso por outro profissional.");
        }

        validarCrmv(request.cargo(), request.crmv());

        funcionario.setNome(request.nome().trim());
        funcionario.setCpf(request.cpf().trim());
        funcionario.setEmail(request.email().trim().toLowerCase());
        funcionario.setTelefone(request.telefone().trim());
        funcionario.setCargo(request.cargo());
        funcionario.setCrmv(request.cargo() == PerfilFuncionario.VETERINARIO ? request.crmv().trim() : null);

        Funcionario atualizado = funcionarioRepository.save(funcionario);
        return FuncionarioDTO.Response.fromEntity(atualizado);
    }

    @Transactional
    public void alternarStatusAtivo(Long id) {
        Funcionario funcionario = obterEntidade(id);
        funcionario.setAtivo(!funcionario.getAtivo());
        funcionarioRepository.save(funcionario);
    }

    @Transactional
    public void remover(Long id) {
        Funcionario funcionario = obterEntidade(id);
        funcionarioRepository.delete(funcionario);
    }

    private void validarCrmv(PerfilFuncionario cargo, String crmv) {
        if (cargo == PerfilFuncionario.VETERINARIO) {
            if (crmv == null || crmv.trim().isBlank()) {
                throw new RegraDeNegocioException("O registro profissional no CRMV é obrigatório para Médicos Veterinários (RN03).");
            }
        }
    }
}
