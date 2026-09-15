package br.com.clinicaveterinaria.controller;

import br.com.clinicaveterinaria.dto.VacinaTratamentoDTO;
import br.com.clinicaveterinaria.service.VacinaTratamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vacinas")
@Tag(name = "Vacinação & Imunizações", description = "Endpoints para registro e controle de vacinas, vermífugos e tratamentos preventivos")
public class VacinaController {

    private final VacinaTratamentoService vacinaService;

    public VacinaController(VacinaTratamentoService vacinaService) {
        this.vacinaService = vacinaService;
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Listar carteira de vacinas do pet", description = "Retorna histórico de imunizações e tratamentos aplicados com próximas doses")
    public ResponseEntity<List<VacinaTratamentoDTO.Response>> listarPorPet(@PathVariable Long petId) {
        return ResponseEntity.ok(vacinaService.listarPorPet(petId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar registro de vacina por ID")
    public ResponseEntity<VacinaTratamentoDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vacinaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar nova vacina / dose aplicada")
    public ResponseEntity<VacinaTratamentoDTO.Response> cadastrar(@Valid @RequestBody VacinaTratamentoDTO.Request request) {
        VacinaTratamentoDTO.Response response = vacinaService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover registro de vacina")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        vacinaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
