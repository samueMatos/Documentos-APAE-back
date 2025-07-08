package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.aluno.AlunoRequestDTO;
import br.apae.ged.application.services.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
@Tag(name = "Alunos", description = "Gerenciamento de Alunos")
@PreAuthorize("hasAuthority('ALUNOS')")
public class AlunoController {

    private final AlunoService alunoService;
    private static final Logger logger = LoggerFactory.getLogger(AlunoController.class);



    @PostMapping("/create")
    @Operation(summary = "Cria um novo aluno", description = "Cria um novo aluno com as informações fornecidas no corpo da requisição.")
    public ResponseEntity<?> post(@RequestBody AlunoRequestDTO request) {
        try {
            return ResponseEntity.status(201).body(alunoService.create(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping(value = "/importar", consumes = "multipart/form-data")
    @Operation(summary = "Importa alunos de um arquivo", description = "Importa uma lista de alunos a partir de um arquivo Excel.")
    public ResponseEntity<?> importarAlunos(@RequestParam("file") MultipartFile file) {
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

    @GetMapping("/all")
    @Operation(summary = "Lista todos os alunos", description = "Retorna uma lista de alunos. A busca pode ser feita por nome, CPF ou matrícula usando o parâmetro 'termoBusca'.")
    public ResponseEntity<?> findAll(@RequestParam(required = false) String termoBusca,
                                     @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        try {

            var paginaDeAlunos = alunoService.findAll(termoBusca, pageable);
            return ResponseEntity.ok(paginaDeAlunos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um aluno", description = "Atualiza as informações de um aluno existente com base no ID fornecido.")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody AlunoRequestDTO aluno) {
        try {
            return ResponseEntity.ok(alunoService.update(id, aluno));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um aluno por ID", description = "Retorna as informações de um aluno específico com base no ID fornecido.")
    public ResponseEntity<?> byID(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(alunoService.byID(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativa um aluno", description = "Desativa um aluno existente com base no ID fornecido, alterando seu status para inativo.")
    public ResponseEntity<?> desativarAluno(@PathVariable("id") Long id) {
        try {
            alunoService.changeStatusAluno(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}