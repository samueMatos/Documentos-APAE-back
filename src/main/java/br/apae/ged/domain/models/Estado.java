package br.apae.ged.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter @Table(name = "tb_estado")
public class Estado {

    @Id
    private Long id;
    private String nome;
    private String uf;

}
