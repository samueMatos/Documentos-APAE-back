package br.apae.ged.application.dto.document;

import br.apae.ged.domain.models.Document;
import br.apae.ged.domain.models.Pessoa; // Importe a entidade base
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

public record DocumentResponseDTO(
        Long id,
        String titulo,
        String tipoConteudo,
        String documento,
        LocalDateTime dataUpload,
        LocalDateTime dataDownload,
        LocalDate dataDocumento,
        PessoaResponseDTO pessoa, // Campo renomeado para refletir a entidade base
        TipoDocumentoResponseDTO tipoDocumento) {

    // Record interno agora representa uma Pessoa genérica
    public record PessoaResponseDTO(
            Long id,
            String nome) {
        public static PessoaResponseDTO fromEntity(Pessoa pessoa) {
            if (pessoa == null)
                return null;
            return new PessoaResponseDTO(pessoa.getId(), pessoa.getNome());
        }
    }

    public record TipoDocumentoResponseDTO(
            Long id,
            String nome) {
        public static TipoDocumentoResponseDTO fromEntity(br.apae.ged.domain.models.TipoDocumento tipo) {
            if (tipo == null)
                return null;
            return new TipoDocumentoResponseDTO(tipo.getId(), tipo.getNome());
        }
    }

    // Método de fábrica para criar o DTO a partir da entidade Document com conteúdo
    public static DocumentResponseDTO fromEntity(Document document, byte[] documentoBytes) {
        String base64Content = (documentoBytes != null) ? Base64.getEncoder().encodeToString(documentoBytes) : null;
        return new DocumentResponseDTO(
                document.getId(),
                document.getTitulo(),
                document.getTipoConteudo(),
                base64Content,
                document.getDataUpload(),
                document.getDataDownload(),
                document.getDataDocumento(),
                PessoaResponseDTO.fromEntity(document.getPessoa()), // Utiliza o novo relacionamento
                TipoDocumentoResponseDTO.fromEntity(document.getTipoDocumento()));
    }

    // Método de fábrica para criar o DTO sem o conteúdo do arquivo
    public static DocumentResponseDTO fromEntityWithoutContent(Document document) {
        return new DocumentResponseDTO(
                document.getId(),
                document.getTitulo(),
                document.getTipoConteudo(),
                null,
                document.getDataUpload(),
                document.getDataDownload(),
                document.getDataDocumento(),
                PessoaResponseDTO.fromEntity(document.getPessoa()), // Utiliza o novo relacionamento
                TipoDocumentoResponseDTO.fromEntity(document.getTipoDocumento()));
    }
}