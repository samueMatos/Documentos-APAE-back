package br.apae.ged.application.dto.tipoDocumento;

import java.sql.Date;

public record TipoDocumentoRequest (
        String nome,
        Date validade
){}
