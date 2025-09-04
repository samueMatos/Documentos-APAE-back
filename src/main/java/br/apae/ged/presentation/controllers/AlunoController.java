package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.aluno.AlunoByIdResponse;
import br.apae.ged.application.dto.aluno.AlunoRequestDTO;
import br.apae.ged.application.dto.aluno.AlunoResponseDTO;
import br.apae.ged.application.services.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
@Tag(name = "Alunos", description = "Endpoints para o gerenciamento completo de Alunos.")
@PreAuthorize("hasAuthority('ALUNOS')")
public class AlunoController {

    private final AlunoService alunoService;
    private static final Logger logger = LoggerFactory.getLogger(AlunoController.class);

    @Operation(summary = "Cadastra um novo aluno", description = "Cria um novo aluno no sistema com base nos dados fornecidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aluno criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF já existente.", content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<?> post(@RequestBody AlunoRequestDTO request) {
        try {
            return ResponseEntity.status(201).body(alunoService.create(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Importa uma lista de alunos via arquivo Excel", description = "Realiza o upload de um arquivo .xlsx para cadastrar múltiplos alunos em lote.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Alunos importados com sucesso."),
            @ApiResponse(responseCode = "400", description = "Arquivo vazio, inválido ou com dados incorretos.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro inesperado no servidor durante o processamento do arquivo.", content = @Content)
    })
    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importarAlunos(
            @Parameter(description = "Arquivo Excel (.xlsx) contendo os dados dos alunos.") @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("O arquivo não pode ser vazio.");
        }
        try {
            var alunosImportados = alunoService.importarAlunos(file.getInputStream());
            return ResponseEntity.status(201).body(alunosImportados);
        } catch (IllegalArgumentException e) {
            logger.warn("Falha na validação da importação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao importar alunos: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Ocorreu um erro inesperado no servidor ao processar o arquivo.");
        }
    }

    @Operation(summary = "Lista todos os alunos de forma paginada", description = "Retorna uma lista paginada de alunos. A busca pode ser filtrada por nome, CPF ou matrícula.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alunos retornada com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<?> findAll(
            @Parameter(description = "Termo para busca por nome, CPF ou matrícula do aluno.") @RequestParam(required = false) String termoBusca,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        try {
            var paginaDeAlunos = alunoService.findAll(termoBusca, pageable);
            return ResponseEntity.ok(paginaDeAlunos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Busca um aluno específico por ID", description = "Retorna os detalhes completos de um aluno, incluindo informações de endereço.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno encontrado com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlunoByIdResponse.class))),
            @ApiResponse(responseCode = "400", description = "Aluno não encontrado para o ID informado.", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> byID(@Parameter(description = "ID do aluno a ser buscado.") @PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(alunoService.byID(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Atualiza os dados de um aluno", description = "Atualiza as informações de um aluno existente com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou aluno não encontrado.", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Parameter(description = "ID do aluno a ser atualizado.") @PathVariable("id") Long id,
            @RequestBody AlunoRequestDTO aluno) {
        try {
            return ResponseEntity.ok(alunoService.update(id, aluno));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Ativa ou inativa um aluno", description = "Altera o status de um aluno (de ativo para inativo e vice-versa) com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status do aluno alterado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Aluno não encontrado.", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desativarAluno(
            @Parameter(description = "ID do aluno para alterar o status.") @PathVariable("id") Long id) {
        try {
            alunoService.changeStatusAluno(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}