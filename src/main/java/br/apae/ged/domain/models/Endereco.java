package br.apae.ged.domain.models;


import br.apae.ged.application.dto.aluno.AlunoRequestDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SuperBuilder
@Entity(name = "tb_endereco")
public class Endereco extends EntityID{

    private String bairro;
    private String rua;
    private int numero;
    private String complemento;
    private String cep;

    @OneToOne
    @JoinColumn(name = "aluno_id", referencedColumnName = "id")
    private Alunos aluno;
    @ManyToOne
    @JoinColumn(name = "cidade_id", referencedColumnName = "id")
    private Cidade cidade;


    public Endereco(Cidade cidade,
                    String bairro,
                    String rua,
                    int numero,
                    String complemento,
                    String cep) {
        this.cidade = cidade;
        this.bairro = bairro;
        this.rua = rua;
        this.numero = numero;
        this.complemento = complemento;
        this.cep = cep;
    }

    public static Endereco paraEntidade(AlunoRequestDTO requestDTO, Cidade cidade){
        return new Endereco(
                cidade,
                requestDTO.bairro(),
                requestDTO.rua(),
                requestDTO.numero(),
                requestDTO.complemento(),
                requestDTO.cep()
        );
    }

    public void atualizarDados(AlunoRequestDTO atualizacao, Cidade cidade) {
        this.setBairro(atualizacao.bairro());
        this.setRua(atualizacao.rua());
        this.setNumero(atualizacao.numero());
        this.setCep(atualizacao.cep());
        this.setCidade(cidade);
        this.setComplemento(atualizacao.complemento());
    }
}
