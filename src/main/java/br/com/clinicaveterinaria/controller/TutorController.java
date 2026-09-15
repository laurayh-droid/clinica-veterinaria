package br.com.clinicaveterinaria.controller;

import br.com.clinicaveterinaria.dto.TutorDTO;
import br.com.clinicaveterinaria.service.TutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutores")
@Tag(name = "Tutores", description = "Endpoints para gerenciamento de tutores e responsáveis por animais")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os tutores", description = "Retorna lista de tutores com seus pets associados e suporte a busca")
    public ResponseEntity<List<TutorDTO.Response>> listar(@RequestParam(value = "busca", required = false) String busca) {
        if (busca != null && !busca.isBlank()) {
            return ResponseEntity.ok(tutorService.buscar(busca));
        }
        return ResponseEntity.ok(tutorService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tutor por ID")
    public ResponseEntity<TutorDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tutorService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar tutor", description = "Cadastra novo tutor, opcionalmente incluindo dados do seu primeiro paciente pet")
    public ResponseEntity<TutorDTO.Response> cadastrar(@Valid @RequestBody TutorDTO.Request request) {
        TutorDTO.Response response = tutorService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados do tutor")
    public ResponseEntity<TutorDTO.Response> atualizar(@PathVariable Long id, @Valid @RequestBody TutorDTO.Request request) {
        return ResponseEntity.ok(tutorService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover tutor do sistema")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        tutorService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
