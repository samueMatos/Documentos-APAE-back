package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.colaborador.ColaboradorRequestDTO;
import br.apae.ged.application.services.ColaboradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/colaboradores")
@RequiredArgsConstructor
@Tag(name = "Colaboradores", description = "Gerenciamento de Colaboradores")
@PreAuthorize("hasAuthority('COLABORADORES')")
public class ColaboradorController {

    private final ColaboradorService colaboradorService;

    @PostMapping("/create")
    @Operation(summary = "Cria um novo colaborador")
    public ResponseEntity<?> create(@RequestBody ColaboradorRequestDTO request) {
        try {
            return ResponseEntity.status(201).body(colaboradorService.create(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Lista todos os colaboradores de forma paginada")
    public ResponseEntity<?> findAll(@PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        try {
            return ResponseEntity.ok(colaboradorService.findAll(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um colaborador por ID")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(colaboradorService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um colaborador")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ColaboradorRequestDTO request) {
        try {
            return ResponseEntity.ok(colaboradorService.update(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Ativa/Inativa um colaborador")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        try {
            colaboradorService.changeStatus(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}