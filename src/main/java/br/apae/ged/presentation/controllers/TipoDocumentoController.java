package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.tipoDocumento.TipoDocumentoRequest;
import br.apae.ged.application.dto.tipoDocumento.TipoDocumentoResponse;
import br.apae.ged.application.services.TipoDocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@Tag(name = "Tipos de Documento", description = "Endpoints para gerenciamento dos tipos de documento.")
public class TipoDocumentoController {

    private final TipoDocumentoService tipoDocumentoService;

    @Operation(summary = "Cria um novo tipo de documento", description = "Cadastra um novo tipo de documento no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo de documento criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Já existe um tipo de documento ativo com este nome.", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody TipoDocumentoRequest request) {
        try {
            return ResponseEntity.status(201).body(tipoDocumentoService.create(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Lista os tipos de documento de forma paginada", description = "Retorna uma lista paginada de tipos de documento, com busca opcional por nome.")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso.")
    @GetMapping("/all")
    public ResponseEntity<?> findAll(
            @Parameter(description = "Termo para busca por nome do tipo de documento.") @RequestParam(required = false) String termoBusca,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        try {
            var paginaDeTipos = tipoDocumentoService.findAll(termoBusca, pageable);
            return ResponseEntity.ok(paginaDeTipos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Busca tipo de documento por ID", description = "Retorna os detalhes de um tipo de documento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de documento encontrado."),
            @ApiResponse(responseCode = "404", description = "Tipo de documento não encontrado.", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@Parameter(description = "ID do tipo de documento.") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(tipoDocumentoService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Atualiza um tipo de documento", description = "Atualiza as informações de um tipo de documento existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de documento atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Nome do tipo de documento já em uso por outro registro.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tipo de documento não encontrado.", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Parameter(description = "ID do tipo de documento a ser atualizado.") @PathVariable Long id,
            @RequestBody TipoDocumentoRequest request) {
        try {
            return ResponseEntity.ok(tipoDocumentoService.update(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Ativa ou Inativa um tipo de documento", description = "Altera o status de um tipo de documento (ativo/inativo).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status alterado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Tipo de documento não encontrado.", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(
            @Parameter(description = "ID do tipo de documento para alterar o status.") @PathVariable Long id) {
        try {
            tipoDocumentoService.changeStatus(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Lista todos os tipos de documento ativos", description = "Retorna uma lista não paginada com todos os tipos de documento que estão com status ativo.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.")
    @GetMapping("/ativos")
    public ResponseEntity<List<TipoDocumentoResponse>> findAllAtivos() {
        try {
            List<TipoDocumentoResponse> listaDeTipos = tipoDocumentoService.findAllAtivos();
            return ResponseEntity.ok(listaDeTipos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}