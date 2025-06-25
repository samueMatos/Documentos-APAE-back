package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.aluno.AlunoRequestDTO;
import br.apae.ged.application.services.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
@Tag(name = "Alunos", description = "Gerenciamento de Alunos")
public class AlunoController {

    private final AlunoService alunoService;

    @PostMapping("/create")
    @Operation(summary = "Cria um novo aluno", description = "Cria um novo aluno com as informações fornecidas no corpo da requisição.")
    public ResponseEntity<?> post(@RequestBody AlunoRequestDTO request) {
        try {
            return ResponseEntity.status(201).body(alunoService.create(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Lista todos os alunos", description = "Retorna uma lista de todos os alunos cadastrados, com opções de filtragem por nome e CPF, retornando 10 alunos por lista.")
    public ResponseEntity<?> findAll(@RequestParam(required = false) String nome,
                                     @RequestParam(required = false) String cpf,
                                     @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        try {
            var paginaDeAlunos = alunoService.findAll(cpf, nome, pageable);
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
