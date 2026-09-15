package br.com.clinicaveterinaria.controller;

import br.com.clinicaveterinaria.dto.PetDTO;
import br.com.clinicaveterinaria.enums.CategoriaPet;
import br.com.clinicaveterinaria.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
@Tag(name = "Pets & Pacientes", description = "Endpoints para cadastro e gestão de animais domésticos, silvestres e exóticos")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os pets", description = "Retorna lista de pets com informações do tutor e filtros por busca ou categoria")
    public ResponseEntity<List<PetDTO.Response>> listar(
            @RequestParam(value = "busca", required = false) String busca,
            @RequestParam(value = "categoria", required = false) CategoriaPet categoria,
            @RequestParam(value = "tutorId", required = false) Long tutorId) {

        if (tutorId != null) {
            return ResponseEntity.ok(petService.listarPorTutor(tutorId));
        }
        if (categoria != null) {
            return ResponseEntity.ok(petService.listarPorCategoria(categoria));
        }
        if (busca != null && !busca.isBlank()) {
            return ResponseEntity.ok(petService.buscar(busca));
        }
        return ResponseEntity.ok(petService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pet por ID")
    public ResponseEntity<PetDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(petService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo pet", description = "Cadastra pet vinculado obrigatoriamente a um tutor cadastrado (RN01)")
    public ResponseEntity<PetDTO.Response> cadastrar(@Valid @RequestBody PetDTO.Request request) {
        PetDTO.Response response = petService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cadastro do pet")
    public ResponseEntity<PetDTO.Response> atualizar(@PathVariable Long id, @Valid @RequestBody PetDTO.Request request) {
        return ResponseEntity.ok(petService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover pet do sistema")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        petService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
