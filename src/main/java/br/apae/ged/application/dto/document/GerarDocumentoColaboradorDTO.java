package br.apae.ged.application.dto.document;

public record GerarDocumentoColaboradorDTO(
        String texto,
        String colaborador,
        String tipoDocumento,
        String titulo,
        String textoCabecalho,
        String textoRodape
) {
}
