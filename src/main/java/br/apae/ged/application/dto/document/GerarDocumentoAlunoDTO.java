package br.apae.ged.application.dto.document;

public record GerarDocumentoAlunoDTO(
        String texto,
        Long alunoId,
        String tipoDocumento,
        String textoCabecalho,
        String textoRodape
) {
}
