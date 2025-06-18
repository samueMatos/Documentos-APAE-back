package br.apae.ged.application.dto.tipoDocumento;

import java.time.LocalDateTime;

public record TipoDocumentoRequest (
        String nome,
        LocalDateTime validade
){}
