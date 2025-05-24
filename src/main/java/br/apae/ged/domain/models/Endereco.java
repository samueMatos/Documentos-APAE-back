package br.apae.ged.domain.models;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity(name = "tb_endereco")
public class Endereco extends EntityID{

    private Cidade cidade;
    private String bairro;
    private String rua;
    private int numero;
    private String complemento;
    private String cep;

    @ManyToOne
    @JoinColumn(name = "aluno_id", referencedColumnName = "id")
    private Alunos aluno;

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
}
