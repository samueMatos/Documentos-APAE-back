package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.document.DocumentUploadResponseDTO;
import br.apae.ged.application.dto.documentoIstitucional.*;
import br.apae.ged.application.services.InstitucionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/institucional")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('DOCUMENTOS')")
@Tag(name = "Documentos Institucionais", description = "Controla os serviços relacionados a documentos institucionais.")
public class InstitucionalController {

    private final InstitucionalService service;

    @Operation(summary = "Gera e salva um documento institucional em PDF", description = "Cria um novo documento institucional em formato PDF a partir de dados de texto, salva no sistema e retorna o ID do documento salvo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Documento gerado e salvo com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos.", content = @Content)
    })
    @PostMapping("/gerar-e-salvar")
    public ResponseEntity<DocumentUploadResponseDTO> gerarESalvarPdf(@RequestBody GerarDocInstitucionalRequest entrada)
            throws Exception {
        var response = service.gerarESalvarPdf(entrada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Pré-visualiza um documento institucional em PDF", description = "Gera um documento PDF para visualização sem salvá-lo no sistema. Ideal para prever o resultado final do documento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso.", content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "500", description = "Erro interno ao gerar o PDF.", content = @Content)
    })
    @PostMapping("/pre-visualizar")
    public ResponseEntity<byte[]> gerarPdfPreview(@RequestBody GerarDocInstitucionalRequest entrada) {
        try {
            byte[] pdfBytes = service.gerarPdf(entrada);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=documento_preview.pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Upload de um documento institucional existente", description = "Faz o upload de um arquivo (PDF, DOCX, etc.) e o salva como um documento institucional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Upload realizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Arquivo vazio ou dados inválidos.", content = @Content)
    })
    @PostMapping(value = "/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<InstucionalUploadResponse> upload(
            @Parameter(description = "Título do documento.") @RequestParam("titulo") String titulo,
            @Parameter(description = "Tipo do documento (ex: 'Ata de Reunião').") @RequestParam("tipoDocumento") String tipoDocumento,
            @Parameter(description = "Data de criação do documento (formato: AAAA-MM-DD).") @RequestParam("dataDocumento") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataCriacao,
            @Parameter(description = "Arquivo a ser enviado.") @RequestPart("file") MultipartFile documento) {
        try {
            var response = service
                    .uploadDocumento(new UploadInstitucionalRequest(documento, titulo, dataCriacao, tipoDocumento));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new RuntimeException("Falha no upload do documento institucional.", e);
        }
    }

    @Operation(summary = "Lista documentos institucionais com filtros", description = "Retorna uma lista paginada de documentos institucionais, com filtros opcionais por tipo, título e data de criação.")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso.")
    @GetMapping("/listar")
    public ResponseEntity<Page<InstucionalResponse>> listarDocumentos(
            @Parameter(description = "Filtrar por tipo de documento.") @RequestParam(required = false) String tipoDocumento,
            @Parameter(description = "Filtrar por título do documento.") @RequestParam(required = false) String titulo,
            @Parameter(description = "Filtrar por data de criação (formato: AAAA-MM-DD).") @RequestParam(required = false) LocalDate dataCriacao,
            Pageable pageable) {
        Page<InstucionalResponse> documentos = service.listarDocumentos(tipoDocumento, titulo, dataCriacao, pageable);
        return ResponseEntity.ok(documentos);
    }

    @Operation(summary = "Visualiza um documento institucional por ID", description = "Retorna os detalhes e o conteúdo (em base64) de um único documento institucional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento encontrado."),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado.", content = @Content)
    })
    @GetMapping("/listarUm/{id}")
    public ResponseEntity<?> listarUm(
            @Parameter(description = "ID do documento a ser visualizado.") @PathVariable Long id) {
        var documento = service.visualizarUm(id);
        return ResponseEntity.ok(documento);
    }

    @Operation(summary = "Atualiza um documento institucional", description = "Atualiza os metadados ou o arquivo de um documento institucional existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado.", content = @Content)
    })
    @PutMapping(value = "/atualizar/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<InstucionalResponse> atualizarDocumento(
            @Parameter(description = "ID do documento a ser atualizado.") @PathVariable Long id,
            @ModelAttribute AtualizarInstitucionalRequest dto) throws IOException {
        InstucionalResponse response = service.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Inativa um documento institucional", description = "Marca um documento como inativo, removendo-o das listagens padrão.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Documento inativado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado.", content = @Content)
    })
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarDocumento(
            @Parameter(description = "ID do documento a ser inativado.") @PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}