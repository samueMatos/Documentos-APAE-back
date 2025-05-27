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
    private String cpfResponsavel;
    private String deficiencia;
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


    public Alunos(String nome,
                  LocalDate dataNascimento,
                  String sexo,
                  CPF cpf,
                  String telefone,
                  String cpfResponsavel,
                  String deficiencia,
                  LocalDate dataEntrada,
                  String observacoes) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.cpf = cpf;
        this.telefone = telefone;
        this.cpfResponsavel = cpfResponsavel;
        this.deficiencia = deficiencia;
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
                request.cpfResponsavel(),
                request.deficiencia(),
                LocalDate.now(),
                request.observacoes()
        );
    }
}