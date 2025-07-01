package br.apae.ged.domain.models;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "tb_tipo_documento")
public class TipoDocumento extends EntityID  {

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User usuario;

        @ManyToOne
        @JoinColumn(name = "id_usuario_alteracao")
        private User usuarioAlteracao;

        @Column(name = "data_alteracao")
        private LocalDateTime dataAlteracao;

        @Column(name = "data_registro")
        private LocalDateTime dataRegistro;

        @Column(name = "nome")
        private String nome;

        @Column(name = "validade")
        private LocalDateTime validade;

}
