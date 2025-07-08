package br.apae.ged.domain.models;

import br.apae.ged.application.dto.tipoDocumento.TipoDocumentoRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "tb_tipo_documento")
public class TipoDocumento extends EntityID {

        @Column(name = "nome")
        private String nome;

        @Column(name = "validade")
        private Integer validade;

        private Boolean isAtivo;

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


        public TipoDocumento(String nome, Integer validade, User usuario) {
                this.nome = nome;
                this.validade = validade;
                this.usuario = usuario;
                this.dataRegistro = LocalDateTime.now();
                this.isAtivo = true;
        }

        public static TipoDocumento paraEntidade(TipoDocumentoRequest request, User usuario) {
                return new TipoDocumento(
                        request.nome(),
                        request.validade(),
                        usuario
                );
        }


        public void atualizarDados(TipoDocumentoRequest request, User usuario) {
                this.setNome(request.nome());
                this.setValidade(request.validade());
                this.setUsuarioAlteracao(usuario);
                this.setDataAlteracao(LocalDateTime.now());
        }
}