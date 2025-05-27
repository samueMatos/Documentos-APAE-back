package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.aluno.AlunoRequestDTO;
import br.apae.ged.application.services.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    @PostMapping("/create")
    public ResponseEntity<?> post(@RequestBody AlunoRequestDTO request){
        try {
            return ResponseEntity.status(201).body(alunoService.create(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll(@RequestParam(required = false) String nome,
                                               @RequestParam(required = false) String cpf,
                                               @RequestParam(required = false) String cpfResponsavel,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size){
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(alunoService.findAll(cpf, cpfResponsavel, nome, pageable).getContent());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody AlunoRequestDTO aluno){
        try {
            return ResponseEntity.ok(alunoService.update(id, aluno));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> byID(@PathVariable("id")Long id){
        try {
            return ResponseEntity.ok(alunoService.byID(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> desativarAluno(@PathVariable("id")Long id){
        try {
            alunoService.changeStatusAluno(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
