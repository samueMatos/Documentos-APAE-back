package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.tipoDocumento.TipoDocumentoRequest;
import br.apae.ged.application.dto.tipoDocumento.TipoDocumentoResponse;
import br.apae.ged.application.services.TipoDocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipo-documento")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('TIPO_DOCUMENTO')")
@Tag(name = "Tipos de Documento", description = "Endpoints para gerenciamento dos tipos de documento")
public class TipoDocumentoController {

    private final TipoDocumentoService tipoDocumentoService;

    @PostMapping
    @Operation(summary = "Cria um novo tipo de documento", description = "Registra um novo tipo de documento no sistema.")
    public ResponseEntity<TipoDocumentoResponse> create(@RequestBody TipoDocumentoRequest request) {
        return ResponseEntity.status(201).body(tipoDocumentoService.create(request));
    }

    @GetMapping
    @Operation(summary = "Lista todos os tipos de documento", description = "Retorna uma lista com todos os tipos de documento cadastrados.")
    public ResponseEntity<List<TipoDocumentoResponse>> findAll() {
        return ResponseEntity.ok(tipoDocumentoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca tipo de documento por ID", description = "Retorna os detalhes de um tipo de documento específico.")
    public ResponseEntity<TipoDocumentoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tipoDocumentoService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um tipo de documento", description = "Atualiza os dados de um tipo de documento existente.")
    public ResponseEntity<TipoDocumentoResponse> update(@PathVariable Long id,
            @RequestBody TipoDocumentoRequest request) {
        return ResponseEntity.ok(tipoDocumentoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um tipo de documento", description = "Remove um tipo de documento do sistema com base no ID.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tipoDocumentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}