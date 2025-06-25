package br.apae.ged.domain.models;

import br.apae.ged.application.dto.aluno.AlunoRequestDTO;
import br.apae.ged.domain.valueObjects.CPF;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Entity(name = "tb_aluno")
@Table(indexes = {
        @Index(name = "nome_idx", columnList = "nome"),
        @Index(name = "cpf_idx", columnList = "cpf"),
        @Index(name = "cpf_responsavel_idx", columnList = "cpf_responsavel")
})
public class Alunos extends EntityID {

    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    @Embedded
    private CPF cpf;
    private String telefone;
    private LocalDate dataEntrada;
    private Boolean isAtivo;
    private String observacoes;

    @ManyToOne
    @JoinColumn(name = "registered_by", referencedColumnName = "id")
    @JsonIgnore
    private User createdBy;

    @JsonIgnore
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "updated_by", referencedColumnName = "id")
    @JsonIgnore
    private User updatedBy;

    @JsonIgnore
    private LocalDateTime updatedAt;


    public Alunos(String nome, LocalDate dataNascimento,
                  String sexo, CPF cpf, String telefone,
                  LocalDate dataEntrada, String observacoes) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.cpf = cpf;
        this.telefone = telefone;
        this.dataEntrada = dataEntrada;
        this.createdAt = LocalDateTime.now();
        this.observacoes = observacoes;
    }

    public static Alunos paraEntidade(AlunoRequestDTO request) {
        return new Alunos(
                request.nome(),
                request.dataNascimento(),
                request.sexo(),
                new CPF(request.cpf()),
                request.telefone(),
                request.dataEntrada(),
                request.observacoes()
        );
    }

    public void atualizarDados(AlunoRequestDTO atualizacao, User usuarioAutenticado) {
        this.setNome(atualizacao.nome());
        this.setDataNascimento(atualizacao.dataNascimento());
        this.setSexo(atualizacao.sexo());
        this.setCpf(new CPF(atualizacao.cpf()));
        this.setTelefone(atualizacao.telefone());
        this.setObservacoes(atualizacao.observacoes());
        this.setUpdatedBy(usuarioAutenticado);
        this.setUpdatedAt(LocalDateTime.now());
    }
}