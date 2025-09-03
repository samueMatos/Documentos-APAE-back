package br.apae.ged.domain.models;

import br.apae.ged.domain.valueObjects.CPF;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder; // Importe a anotação correta
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor // Mantenha o construtor sem argumentos para o JPA
@SuperBuilder // Use @SuperBuilder em vez de @Builder
@Entity(name = "tb_pessoa")
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