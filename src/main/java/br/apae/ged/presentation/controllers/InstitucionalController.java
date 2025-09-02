package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.document.DocumentUploadResponseDTO;
import br.apae.ged.application.dto.documentoIstitucional.AtualizarInstitucionalRequest;
import br.apae.ged.application.dto.documentoIstitucional.GerarDocInstitucionalRequest;
import br.apae.ged.application.dto.documentoIstitucional.InstucionalResponse;
import br.apae.ged.application.dto.documentoIstitucional.InstucionalUploadResponse;
import br.apae.ged.application.dto.documentoIstitucional.UploadInstitucionalRequest;
import br.apae.ged.application.services.InstitucionalService;
import io.swagger.v3.oas.annotations.Operation;
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
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/institucional")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('DOCUMENTOS')")
@Tag(name = "Institucional", description = "Controla os serviços relacionados a documentos institucionais")
public class InstitucionalController {

    private final InstitucionalService service;

    @PostMapping("/gerar-e-salvar")
    @Operation(summary = "Gera e salva um documento institucional", description = "Gera um documento em PDF a partir de um texto e salva no sistema")
    public ResponseEntity<DocumentUploadResponseDTO> gerarESalvarPdf(@RequestBody GerarDocInstitucionalRequest entrada) throws Exception {
       var response = service.gerarESalvarPdf(entrada);
       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/pre-visualizar")
    @Operation(summary = "Gera um PDF institucional para visualização", description = "Cria um documento PDF a partir de um texto para pré-visualização, sem salvar no sistema.")
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

    @PostMapping(value = "/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "Realiza um upload de um documento institucional", description = "Faz o upload de um documento já existente no computador do usuário")
    public ResponseEntity<InstucionalUploadResponse> upload(
            @RequestParam("titulo") String titulo,
            @RequestParam("tipoDocumento") String tipoDocumento,
            @RequestParam("dataDocumento") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataCriacao,
            @RequestPart("file") MultipartFile documento) {
        try {
            var response = service.uploadDocumento(new UploadInstitucionalRequest(documento, titulo, dataCriacao, tipoDocumento));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new RuntimeException("Falha no upload do documento institucional.", e);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<InstucionalResponse>> listarDocumentos(
            @RequestParam(required = false) String tipoDocumento,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) LocalDate dataCriacao,
            Pageable pageable
    ) {
        Page<InstucionalResponse> documentos = service.listarDocumentos(tipoDocumento, titulo, dataCriacao, pageable);
        return ResponseEntity.ok(documentos);
    }

    @GetMapping("/listarUm/{id}")
    @Operation(summary = "Listar um documento institucional", description = "Retorna um único documento institucional com seu conteúdo em base64.")
    public ResponseEntity<?> listarUm(@PathVariable Long id) {
        var documento = service.visualizarUm(id);
        return ResponseEntity.ok(documento);
    }

    @PutMapping(value = "/atualizar/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<InstucionalResponse> atualizarDocumento(
            @PathVariable Long id,
            @ModelAttribute AtualizarInstitucionalRequest dto) throws IOException {
        InstucionalResponse response = service.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarDocumento(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
