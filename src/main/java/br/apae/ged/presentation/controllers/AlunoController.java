package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.aluno.AlunoRequestDTO;
import br.apae.ged.domain.models.Alunos;
import br.apae.ged.application.services.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
@Tag(name = "Alunos", description = "Endpoints para gerenciamento de alunos")
public class AlunoController {

    private final AlunoService alunoService;

    @PostMapping("/create")
    @Operation(summary = "Cria um novo aluno", description = "Registra um novo aluno e seu endereço no sistema.")
    public ResponseEntity<Alunos> post(@RequestBody AlunoRequestDTO request) {
        return ResponseEntity.status(201).body(alunoService.create(request));
    }

    @GetMapping("/all")
    @Operation(summary = "Lista todos os alunos", description = "Retorna uma lista paginada de alunos ativos, com opção de filtro por nome, CPF ou CPF do responsável.")
    public ResponseEntity<List<Alunos>> findAll(@RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String cpfResponsavel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(alunoService.findAll(cpf, cpfResponsavel, nome, pageable).getContent());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um aluno", description = "Atualiza os dados de um aluno existente com base no ID fornecido.")
    public ResponseEntity<Alunos> update(@PathVariable("id") Long id, @RequestBody AlunoRequestDTO aluno) {
        return ResponseEntity.ok(alunoService.update(id, aluno));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca aluno por ID", description = "Retorna os detalhes de um aluno específico com base no seu ID.")
    public ResponseEntity<Alunos> byID(@PathVariable("id") Long id) {
        return ResponseEntity.ok(alunoService.byID(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Ativa/Desativa um aluno", description = "Altera o status de um aluno para ativo ou inativo. Requer permissão de ADMIN.")
    public ResponseEntity<Void> desativarAluno(@PathVariable("id") Long id) {
        alunoService.changeStatusAluno(id);
        return ResponseEntity.ok().build();
    }
}