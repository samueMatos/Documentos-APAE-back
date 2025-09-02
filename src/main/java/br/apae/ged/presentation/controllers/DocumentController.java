package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.document.DocumentRequestDTO;
import br.apae.ged.application.dto.document.DocumentResponseDTO;
import br.apae.ged.application.dto.document.GerarDocumentoAlunoDTO;
import br.apae.ged.application.dto.document.DocumentUploadResponseDTO;
import br.apae.ged.application.services.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Documentos", description = "Endpoints para upload, download e gerenciamento de documentos")
public class DocumentController {

    private final DocumentService service;

    @PostMapping(value = "/create/{alunoID}", consumes = "multipart/form-data")
    @Operation(summary = "Upload de um novo documento", description = "Realiza o upload de um arquivo e o associa a um aluno específico.")
    public ResponseEntity<DocumentUploadResponseDTO> post(@ModelAttribute DocumentRequestDTO document,
                                                          @PathVariable("alunoID") Long id) throws IOException {
        return ResponseEntity.status(201).body(service.save(document, id));
    }

    @GetMapping("/listar/aluno/{alunoId}")
    @Operation(summary = "Lista documentos paginados por aluno", description = "Retorna uma lista paginada com a última versão dos documentos para um aluno específico, permitindo filtro por título do documento.")
    public ResponseEntity<?> visualizarTodos(
            @PathVariable Long alunoId,
            @RequestParam(required = false) String termoBusca,
            Pageable pageable
    ) {
        var paginaDeDocumentos = service.listarPorAluno(alunoId, termoBusca, pageable);
        return ResponseEntity.ok(paginaDeDocumentos);
    }

    @GetMapping(value = "/listarUm/{id}")
    @Operation(summary = "Listar um documento", description = "Retorna um único documento com seu conteúdo em base64.")
    public ResponseEntity<?> listarUm(@PathVariable Long id) {
        var documento = service.visualizarUm(id);
        return ResponseEntity.ok(documento);
    }

    @PutMapping(value = "/update/{id}", consumes = "multipart/form-data")
    @Operation(summary = "Atualiza um documento", description = "Atualiza o tipo de documento ou o arquivo de um documento existente.")
    public ResponseEntity<DocumentResponseDTO> update(
            @PathVariable Long id,
            @ModelAttribute DocumentRequestDTO dto) throws IOException {
        DocumentResponseDTO updatedDocument = service.update(id, dto);
        return ResponseEntity.ok(updatedDocument);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Altera o status de um documento", description = "Ativa ou inativa um documento com base no seu ID.")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id) {
        service.changeStatus(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/aluno/gerar-pdf")
    @Operation(summary = "Gera, salva e retorna um documento PDF", description = "Cria um documento PDF, salva no banco de dados associado a um aluno e retorna o arquivo para download.")
    public ResponseEntity<byte[]> gerarPdf(@RequestBody GerarDocumentoAlunoDTO dto) {
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

    @PostMapping("/gerar-e-salvar")
    @Operation(summary = "Gera e salva um novo documento PDF", description = "Cria um documento PDF a partir de textos e o salva, associando-o a um aluno.")
    public ResponseEntity<DocumentUploadResponseDTO> gerarESalvarPdf(@RequestBody GerarDocumentoAlunoDTO dto) throws IOException {
        DocumentUploadResponseDTO response = service.gerarESalvarPdf(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}