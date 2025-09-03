package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.documento_institucional.DocumentoInstitucionalRequestDTO;
import br.apae.ged.application.services.DocumentoInstitucionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documentos-institucionais")
@RequiredArgsConstructor
@Tag(name = "Documentos Institucionais", description = "Gerenciamento de Documentos da Instituição")
@PreAuthorize("hasAuthority('DOCUMENTOS_INSTITUCIONAIS')")
public class DocumentoInstitucionalController {

    private final DocumentoInstitucionalService service;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    @Operation(summary = "Upload de um novo documento institucional")
    public ResponseEntity<?> create(@ModelAttribute DocumentoInstitucionalRequestDTO request) {
        try {
            return ResponseEntity.status(201).body(service.save(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Lista todos os documentos institucionais de forma paginada")
    public ResponseEntity<?> findAll(@PageableDefault(size = 10, sort = "titulo") Pageable pageable) {
        try {
            return ResponseEntity.ok(service.findAll(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um documento institucional")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}