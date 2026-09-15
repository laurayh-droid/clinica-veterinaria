package br.com.clinicaveterinaria.controller;

import br.com.clinicaveterinaria.dto.FuncionarioDTO;
import br.com.clinicaveterinaria.service.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
@Tag(name = "Funcionários & Corpo Clínico", description = "Endpoints para gerenciamento da equipe médica, adestradores e recepção")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os funcionários", description = "Retorna lista de funcionários cadastrados com suporte a filtro por termo (nome/CPF)")
    public ResponseEntity<List<FuncionarioDTO.Response>> listar(@RequestParam(value = "busca", required = false) String busca) {
        if (busca != null && !busca.isBlank()) {
            return ResponseEntity.ok(funcionarioService.buscar(busca));
        }
        return ResponseEntity.ok(funcionarioService.listarTodos());
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar apenas funcionários ativos")
    public ResponseEntity<List<FuncionarioDTO.Response>> listarAtivos() {
        return ResponseEntity.ok(funcionarioService.listarAtivos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar funcionário por ID")
    public ResponseEntity<FuncionarioDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(funcionarioService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo funcionário", description = "Cadastra membro da equipe com validação obrigatória de CRMV se o cargo for Veterinário")
    public ResponseEntity<FuncionarioDTO.Response> cadastrar(@Valid @RequestBody FuncionarioDTO.Request request) {
        FuncionarioDTO.Response response = funcionarioService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar funcionário existente")
    public ResponseEntity<FuncionarioDTO.Response> atualizar(@PathVariable Long id, @Valid @RequestBody FuncionarioDTO.Request request) {
        return ResponseEntity.ok(funcionarioService.atualizar(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alternar status ativo/inativo do funcionário")
    public ResponseEntity<Void> alternarStatus(@PathVariable Long id) {
        funcionarioService.alternarStatusAtivo(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover funcionário do sistema")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        funcionarioService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
