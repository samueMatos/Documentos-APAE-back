package br.apae.ged.application.dto.document;

public record GerarDocumentoDTO(String texto,
                                String aluno,
                                String colaborador,
                                String instituicao,
                                String tipoDocumento,
                                String titulo,
                                String textoCabecalho,
                                String textoRodape
) {
}
