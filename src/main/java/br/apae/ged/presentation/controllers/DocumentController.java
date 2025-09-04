package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.document.DocumentRequestDTO;
import br.apae.ged.application.dto.document.DocumentResponseDTO;
import br.apae.ged.application.dto.document.GerarDocumentoPessoaDTO;
import br.apae.ged.application.dto.document.DocumentUploadResponseDTO;
import br.apae.ged.application.services.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documentos")
@PreAuthorize("hasAuthority('DOCUMENTOS')")
@Tag(name = "Documentos", description = "Endpoints para upload, download e gerenciamento de documentos de pessoas (Alunos/Colaboradores).")
public class DocumentController {

    private final DocumentService service;

    @Operation(summary = "Upload de um novo documento para uma pessoa", description = "Realiza o upload de um arquivo e o associa a uma pessoa (aluno ou colaborador) específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Upload realizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou pessoa não encontrada.", content = @Content)
    })
    @PostMapping(value = "/create/{pessoaId}", consumes = "multipart/form-data")
    public ResponseEntity<DocumentUploadResponseDTO> post(
            @ModelAttribute DocumentRequestDTO document,
            @Parameter(description = "ID da Pessoa (Aluno ou Colaborador) à qual o documento será associado.") @PathVariable("pessoaId") Long id)
            throws IOException {
        return ResponseEntity.status(201).body(service.save(document, id));
    }

    @Operation(summary = "Lista documentos de uma pessoa", description = "Retorna uma lista paginada com a última versão dos documentos para uma pessoa específica, permitindo filtro por título.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documentos listados com sucesso.")
    })
    @GetMapping("/listar/pessoa/{pessoaId}")
    public ResponseEntity<?> visualizarTodos(
            @Parameter(description = "ID da Pessoa (Aluno ou Colaborador) para listar os documentos.") @PathVariable Long pessoaId,
            @RequestParam(required = false) String termoBusca,
            Pageable pageable) {
        var paginaDeDocumentos = service.listarPorPessoa(pessoaId, termoBusca, pageable);
        return ResponseEntity.ok(paginaDeDocumentos);
    }

    @Operation(summary = "Visualiza um documento específico", description = "Retorna os detalhes de um único documento, incluindo seu conteúdo em base64.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento retornado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado.", content = @Content)
    })
    @GetMapping(value = "/listarUm/{id}")
    public ResponseEntity<?> listarUm(
            @Parameter(description = "ID do documento a ser visualizado.") @PathVariable Long id) {
        var documento = service.visualizarUm(id);
        return ResponseEntity.ok(documento);
    }

    @Operation(summary = "Atualiza um documento", description = "Atualiza o tipo de documento ou o arquivo de um documento existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado.", content = @Content)
    })
    @PutMapping(value = "/update/{id}", consumes = "multipart/form-data")
    public ResponseEntity<DocumentResponseDTO> update(
            @Parameter(description = "ID do documento a ser atualizado.") @PathVariable Long id,
            @ModelAttribute DocumentRequestDTO dto) throws IOException {
        DocumentResponseDTO updatedDocument = service.update(id, dto);
        return ResponseEntity.ok(updatedDocument);
    }

    @Operation(summary = "Ativa ou inativa um documento", description = "Altera o status de um documento (ativo/inativo).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Status alterado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado.", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @Parameter(description = "ID do documento para alterar o status.") @PathVariable Long id) {
        service.changeStatus(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Gera um documento PDF para uma pessoa", description = "Cria um documento PDF, salva no banco de dados associado a uma pessoa e retorna o arquivo para download.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso."),
            @ApiResponse(responseCode = "500", description = "Erro ao gerar o PDF.", content = @Content)
    })
    @PostMapping("/pessoa/gerar-pdf")
    public ResponseEntity<byte[]> gerarPdf(@RequestBody GerarDocumentoPessoaDTO dto) {
        try {
            byte[] pdfBytes = service.gerarPdf(dto);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=documento.pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Gera e salva um novo documento PDF para uma pessoa", description = "Cria um documento PDF a partir de textos e o salva, associando-o a uma pessoa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "PDF gerado e salvo com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content)
    })
    @PostMapping("/gerar-e-salvar")
    public ResponseEntity<DocumentUploadResponseDTO> gerarESalvarPdf(@RequestBody GerarDocumentoPessoaDTO dto)
            throws IOException {
        DocumentUploadResponseDTO response = service.gerarESalvarPdf(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}