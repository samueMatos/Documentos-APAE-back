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
@Entity(name = "tb_aluno")
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

    /**
     * Método estático para criar uma nova instância de Aluno a partir de um DTO.
     * Preenche os campos herdados de Pessoa e os campos específicos de Alunos.
     * 
     * @param request   DTO com os dados do aluno.
     * @param createdBy Usuário que está criando o registro.
     * @return Uma nova instância de Alunos preenchida.
     */
    public static Alunos paraEntidade(AlunoRequestDTO request, User createdBy) {
        Alunos aluno = new Alunos();
        aluno.setNome(request.nome());
        aluno.setCpf(new CPF(request.cpf()));
        aluno.setDataNascimento(request.dataNascimento());
        aluno.setSexo(request.sexo());
        aluno.setMatricula(request.matricula());
        aluno.setTelefone(request.telefone());
        aluno.setDataEntrada(request.dataEntrada());
        aluno.setObservacoes(request.observacoes());
        aluno.setIsAtivo(true);
        aluno.setCreatedBy(createdBy);
        aluno.setCreatedAt(LocalDateTime.now());
        return aluno;
    }

    /**
     * Atualiza os dados de um aluno existente a partir de um DTO.
     * 
     * @param atualizacao        DTO com os novos dados.
     * @param usuarioAutenticado Usuário que está realizando a alteração.
     */
    public void atualizarDados(AlunoRequestDTO atualizacao, User usuarioAutenticado) {
        this.setNome(atualizacao.nome());
        this.setDataNascimento(atualizacao.dataNascimento());
        this.setSexo(atualizacao.sexo());
        this.setCpf(new CPF(atualizacao.cpf()));
        this.setMatricula(atualizacao.matricula());
        this.setTelefone(atualizacao.telefone());
        this.setObservacoes(atualizacao.observacoes());
        this.setUpdatedBy(usuarioAutenticado);
        this.setUpdatedAt(LocalDateTime.now());
    }
}