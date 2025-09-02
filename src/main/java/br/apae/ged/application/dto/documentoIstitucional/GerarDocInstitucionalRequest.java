package br.apae.ged.application.dto.documentoIstitucional;

public record GerarDocInstitucionalRequest(
        String titulo,
        String texto,
        String rodape,
        String tipoDocumento

) {
}
