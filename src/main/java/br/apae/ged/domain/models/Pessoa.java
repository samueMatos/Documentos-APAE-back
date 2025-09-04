package br.apae.ged.domain.models;

import br.apae.ged.domain.valueObjects.CPF;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tb_pessoa")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Pessoa extends EntityID {

    private String nome;

    @Embedded
    private CPF cpf;

    private Boolean isAtivo;

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
}