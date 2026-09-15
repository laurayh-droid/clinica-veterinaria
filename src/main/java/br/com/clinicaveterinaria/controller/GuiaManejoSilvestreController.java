package br.com.clinicaveterinaria.controller;

import br.com.clinicaveterinaria.dto.GuiaManejoSilvestreDTO;
import br.com.clinicaveterinaria.service.GuiaManejoSilvestreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guias-manejo")
@Tag(name = "Guia de Manejo Silvestre & Adestramento", description = "Endpoints para consultoria comportamental, adequação de recinto e bem-estar de animais silvestres e exóticos")
public class GuiaManejoSilvestreController {

    private final GuiaManejoSilvestreService guiaService;

    public GuiaManejoSilvestreController(GuiaManejoSilvestreService guiaService) {
        this.guiaService = guiaService;
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Listar histórico de guias de manejo do pet", description = "Retorna histórico cronológico de consultorias comportamentais e manejo (RN05)")
    public ResponseEntity<List<GuiaManejoSilvestreDTO.Response>> listarPorPet(@PathVariable Long petId) {
        return ResponseEntity.ok(guiaService.listarPorPet(petId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar guia de manejo por ID")
    public ResponseEntity<GuiaManejoSilvestreDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(guiaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar sessão de manejo silvestre", description = "Salva relatório técnico de recinto, enriquecimento e treino preenchido por Especialista em Silvestres / Adestrador (RN02, RN04, RN06)")
    public ResponseEntity<GuiaManejoSilvestreDTO.Response> registrar(@Valid @RequestBody GuiaManejoSilvestreDTO.Request request) {
        GuiaManejoSilvestreDTO.Response response = guiaService.registrarSessaoManejo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
