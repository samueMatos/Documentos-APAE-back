package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.tipoDocumento.TipoDocumentoRequest;
import br.apae.ged.application.dto.tipoDocumento.TipoDocumentoResponse;
import br.apae.ged.application.services.TipoDocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    @Operation(summary = "Cria um novo tipo de documento")
    public ResponseEntity<?> create(@RequestBody TipoDocumentoRequest request) {
        try {
            return ResponseEntity.status(201).body(tipoDocumentoService.create(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Lista os tipos de documento de forma paginada")
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) String termoBusca,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        try {
            var paginaDeTipos = tipoDocumentoService.findAll(termoBusca, pageable);
            return ResponseEntity.ok(paginaDeTipos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca tipo de documento por ID")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(tipoDocumentoService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um tipo de documento")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody TipoDocumentoRequest request) {
        try {
            return ResponseEntity.ok(tipoDocumentoService.update(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Ativa ou Inativa um tipo de documento")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        try {
            tipoDocumentoService.changeStatus(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


        @GetMapping("/ativos")
        @Operation(summary = "Lista todos os tipos de documento ativos (não paginado)")
        public ResponseEntity<List<TipoDocumentoResponse>> findAllAtivos() {
            try {
                List<TipoDocumentoResponse> listaDeTipos = tipoDocumentoService.findAllAtivos();
                return ResponseEntity.ok(listaDeTipos);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
}