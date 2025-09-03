package br.apae.ged.domain.models;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "tb_colaborador")
@PrimaryKeyJoinColumn(name = "id")
public class Colaborador extends Pessoa {

    private String cargo;

}