package br.apae.ged.domain.models;

import br.apae.ged.application.dto.aluno.AlunoRequestDTO;
import br.apae.ged.domain.valueObjects.CPF;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tb_aluno")
@PrimaryKeyJoinColumn(name = "id")
public class Alunos extends Pessoa {

    private LocalDate dataNascimento;
    private String sexo;
    private String matricula;
    private String telefone;
    private LocalDate dataEntrada;
    private String observacoes;

    @OneToOne(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private Endereco endereco;

    public Alunos(LocalDate dataNascimento,
            String sexo, String matricula, String telefone,
            LocalDate dataEntrada, String observacoes) {
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.matricula = matricula;
        this.telefone = telefone;
        this.dataEntrada = dataEntrada;
        this.setCreatedAt(LocalDateTime.now());
        this.observacoes = observacoes;
    }

    public static Alunos paraEntidade(AlunoRequestDTO request) {
        Alunos aluno = new Alunos(
                request.dataNascimento(),
                request.sexo(),
                request.matricula(),
                request.telefone(),
                request.dataEntrada(),
                request.observacoes());
        aluno.setNome(request.nome());
        aluno.setCpf(new CPF(request.cpf()));
        return aluno;
    }

    public void atualizarDados(AlunoRequestDTO atualizacao, User usuarioAutenticado) {
        this.setNome(atualizacao.nome());
        this.setDataNascimento(atualizacao.dataNascimento());
        this.setSexo(atualizacao.sexo());
        this.setCpf(new CPF(atualizacao.cpf()));
        this.matricula = atualizacao.matricula();
        this.setTelefone(atualizacao.telefone());
        this.setObservacoes(atualizacao.observacoes());
        this.setUpdatedBy(usuarioAutenticado);
        this.setUpdatedAt(LocalDateTime.now());
    }
}