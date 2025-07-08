package br.apae.ged.application.dto.tipoDocumento;

public record TipoDocumentoRequest (
        String nome,
        Integer validade,
        Boolean isAtivo
){}
