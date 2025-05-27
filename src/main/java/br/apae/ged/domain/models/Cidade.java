package br.apae.ged.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Setter @Getter @Table(name = "tb_cidade")
public class Cidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nome;
    @ManyToOne
    @JoinColumn(name = "estado_id")
    private Estado estado;
    @Column(unique = true)
    private String ibge;

    public Cidade(Cidade cidade) {
    }

    public Cidade(){}
}
