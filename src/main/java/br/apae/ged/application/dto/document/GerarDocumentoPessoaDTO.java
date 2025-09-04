package br.apae.ged.application.dto.document;

public record GerarDocumentoPessoaDTO(
        String texto,
        Long pessoaId,
        String tipoDocumento,
        String textoCabecalho,
        String textoRodape) {
}