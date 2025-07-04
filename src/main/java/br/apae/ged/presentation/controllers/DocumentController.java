package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.document.DocumentRequestDTO;
import br.apae.ged.application.dto.document.DocumentUploadResponseDTO;
import br.apae.ged.application.services.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
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

    @GetMapping(value = "/listar")
    @Operation(summary = "Lista documentos paginados", description = "Retorna uma lista paginada com a última versão dos documentos, permitindo filtro por nome do aluno.")
    public ResponseEntity<?> visualizarTodos(
            @RequestParam(required = false) String nome,
            Pageable pageable) {
        var paginaDeDocumentos = service.visualizarTodos(nome, pageable);
        return ResponseEntity.ok(paginaDeDocumentos);
    }

    @GetMapping(value = "/listarUm/{id}")
    @Operation(summary = "Listar um documento", description = "Retorna um único documento")
    public ResponseEntity<?> listarUm (@PathVariable Long id) {
        var documento = service.visualizarUm(id);
        return ResponseEntity.ok(documento);
    }


//    @GetMapping("/{id}")
//    @Operation(summary = "Busca histórico de um documento", description = "Retorna o histórico de versões de um documento específico, limitado às últimas 5 versões.")
//    public ResponseEntity<List<DocumentResponseDTO>> byID(@PathVariable("id") Long id) {
//        return ResponseEntity.ok(service.byID(id));
//    }
//
//    @GetMapping("/list")
//    @Operation(summary = "Lista os documentos", description = "Retorna uma lista com a última versão dos documentos, permitindo filtros por ID do aluno, título do documento ou nome do aluno.")
//    public ResponseEntity<List<DocumentResponseDTO>> list(@RequestParam(required = false) Long id,
//            @RequestParam(required = false) String nome,
//            @RequestParam(required = false) String aluno) {
//        return ResponseEntity.ok(service.list(id, nome, aluno));
//    }
//
//    @GetMapping("/download/{id}")
//    @Operation(summary = "Download de um documento", description = "Baixa o arquivo físico de um documento com base no seu ID.")
//    public ResponseEntity<Resource> downloadFile(@PathVariable("id") Long id) throws MalformedURLException {
//        Resource resource = service.downloadFile(id);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }
}