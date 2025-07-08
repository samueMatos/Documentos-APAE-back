package br.apae.ged.application.dto.tipoDocumento;

import java.time.LocalDateTime;

import br.apae.ged.domain.models.TipoDocumento;

public record TipoDocumentoResponse(
        Long id,
        String nome,
        String usuarioRegistro,
        String usuarioAlteracao,
        LocalDateTime dataAlteracao,
        LocalDateTime dataRegistro,
        Integer validade,
        Boolean isAtivo) {

    public TipoDocumentoResponse(TipoDocumento tipoDocumento) {
        this(
                tipoDocumento.getId(),
                tipoDocumento.getNome(),
                tipoDocumento.getUsuario() != null ? tipoDocumento.getUsuario().getNome() : null,
                tipoDocumento.getUsuarioAlteracao() != null ? tipoDocumento.getUsuarioAlteracao().getNome() : null,
                tipoDocumento.getDataAlteracao(),
                tipoDocumento.getDataRegistro(),
                tipoDocumento.getValidade(),
                tipoDocumento.getIsAtivo());
    }
}