package br.com.clinicaveterinaria.controller;

import br.com.clinicaveterinaria.dto.ProntuarioClinicoDTO;
import br.com.clinicaveterinaria.service.ProntuarioClinicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prontuarios")
@Tag(name = "Prontuário Médico Veterinário", description = "Endpoints para registro de anamnese clínica, exame físico, diagnóstico e emissão de receitas com CRMV")
public class ProntuarioClinicoController {

    private final ProntuarioClinicoService prontuarioService;

    public ProntuarioClinicoController(ProntuarioClinicoService prontuarioService) {
        this.prontuarioService = prontuarioService;
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Listar histórico de prontuários clínicos do pet", description = "Retorna histórico cronológico das consultas veterinárias realizadas (RN05)")
    public ResponseEntity<List<ProntuarioClinicoDTO.Response>> listarPorPet(@PathVariable Long petId) {
        return ResponseEntity.ok(prontuarioService.listarPorPet(petId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar prontuário por ID")
    public ResponseEntity<ProntuarioClinicoDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(prontuarioService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar atendimento clínico veterinário", description = "Salva o prontuário preenchido por Médico Veterinário com CRMV e finaliza o agendamento correspondente (RN02, RN03, RN06)")
    public ResponseEntity<ProntuarioClinicoDTO.Response> registrar(@Valid @RequestBody ProntuarioClinicoDTO.Request request) {
        ProntuarioClinicoDTO.Response response = prontuarioService.registrarAtendimento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/receita")
    @Operation(summary = "Emitir receita médica oficial com CRMV", description = "Gera a estrutura formal do receituário com carimbo/assinatura do Médico Veterinário responsável (RN03)")
    public ResponseEntity<ProntuarioClinicoDTO.ReceitaResponse> emitirReceita(@PathVariable Long id) {
        return ResponseEntity.ok(prontuarioService.emitirReceitaOficial(id));
    }
}
