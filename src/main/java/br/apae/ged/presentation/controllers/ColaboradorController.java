package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.colaborador.ColaboradorRequestDTO;
import br.apae.ged.application.dto.colaborador.ColaboradorResponseDTO;
import br.apae.ged.application.services.ColaboradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

@RestController
@RequestMapping("/colaboradores")
@RequiredArgsConstructor
@Tag(name = "Colaboradores", description = "Gerenciamento de Colaboradores")
@PreAuthorize("hasAuthority('GERENCIAR_COLABORADORES')")
public class ColaboradorController {

    private final ColaboradorService colaboradorService;

    @Operation(summary = "Cadastra um novo colaborador", description = "Cria um novo colaborador no sistema com nome, CPF e cargo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Colaborador criado com sucesso.", content = @Content(schema = @Schema(implementation = ColaboradorResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF já existente.", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ColaboradorRequestDTO request) {
        try {
            return ResponseEntity.status(201).body(colaboradorService.create(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Lista todos os colaboradores", description = "Retorna uma lista paginada de todos os colaboradores cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de colaboradores retornada com sucesso.")
    })
    @GetMapping
    public ResponseEntity<Page<ColaboradorResponseDTO>> findAll(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(colaboradorService.findAll(pageable));
    }

    @Operation(summary = "Busca um colaborador por ID", description = "Retorna os detalhes de um colaborador específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colaborador encontrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Colaborador não encontrado para o ID informado.", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(
            @Parameter(description = "ID do colaborador a ser buscado.") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(colaboradorService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Atualiza um colaborador", description = "Atualiza os dados de um colaborador existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colaborador atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou colaborador não encontrado.", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Parameter(description = "ID do colaborador a ser atualizado.") @PathVariable Long id,
            @RequestBody ColaboradorRequestDTO request) {
        try {
            return ResponseEntity.ok(colaboradorService.update(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Exclui um colaborador", description = "Remove permanentemente um colaborador do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Colaborador excluído com sucesso."),
            @ApiResponse(responseCode = "400", description = "Colaborador não encontrado.", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do colaborador a ser excluído.") @PathVariable Long id) {
        try {
            colaboradorService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}