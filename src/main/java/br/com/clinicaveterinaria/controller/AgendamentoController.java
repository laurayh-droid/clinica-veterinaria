package br.com.clinicaveterinaria.controller;

import br.com.clinicaveterinaria.dto.AgendamentoDTO;
import br.com.clinicaveterinaria.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping({"/atendimentos", "/agendamentos"})
@Tag(name = "Agenda & Recepção", description = "Endpoints para agendamento de consultas e gestão da fila de espera do dia")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @GetMapping
    @Operation(summary = "Listar agendamentos", description = "Lista atendimentos com suporte a filtro por data específica")
    public ResponseEntity<List<AgendamentoDTO.Response>> listar(
            @RequestParam(value = "data", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        if (data != null) {
            return ResponseEntity.ok(agendamentoService.listarPorData(data));
        }
        return ResponseEntity.ok(agendamentoService.listarFilaHoje());
    }

    @GetMapping("/fila-hoje")
    @Operation(summary = "Listar fila de atendimentos de hoje")
    public ResponseEntity<List<AgendamentoDTO.Response>> listarFilaHoje() {
        return ResponseEntity.ok(agendamentoService.listarFilaHoje());
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Listar histórico de agendamentos por pet")
    public ResponseEntity<List<AgendamentoDTO.Response>> listarPorPet(@PathVariable Long petId) {
        return ResponseEntity.ok(agendamentoService.listarPorPet(petId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por ID")
    public ResponseEntity<AgendamentoDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Agendar novo atendimento", description = "Cria um novo agendamento com validação de profissional responsável")
    public ResponseEntity<AgendamentoDTO.Response> agendar(@Valid @RequestBody AgendamentoDTO.Request request) {
        AgendamentoDTO.Response response = agendamentoService.agendar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do atendimento na fila", description = "Altera o status (ex: Aguardando, Em Atendimento, Finalizado, Cancelado)")
    public ResponseEntity<AgendamentoDTO.Response> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AgendamentoDTO.StatusUpdateRequest request) {
        return ResponseEntity.ok(agendamentoService.atualizarStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover agendamento")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        agendamentoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
