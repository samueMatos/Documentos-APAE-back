package br.apae.ged.application.dto.tipoDocumento;

import java.sql.Date;
import java.time.LocalDateTime;

import br.apae.ged.domain.models.TipoDocumento;

public record TipoDocumentoResponse(

        String nome,
        String usuarioAlteracao,
        LocalDateTime dataAlteracao,
        LocalDateTime dataRegistro,
        Date validade

) {

    public TipoDocumentoResponse() {
        this(null, null, null, null, null);
    }

    public TipoDocumentoResponse(TipoDocumento tipoDocumento) {

        this(
            tipoDocumento.getNome(),
            tipoDocumento.getUsuarioAlteracao().getNome(),
            tipoDocumento.getDataAlteracao(),
            tipoDocumento.getDataRegistro(),
            tipoDocumento.getValidade()
        );
        
    }
}