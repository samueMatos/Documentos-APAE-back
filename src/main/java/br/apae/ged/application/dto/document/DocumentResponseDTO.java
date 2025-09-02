package br.apae.ged.application.dto.document;

import br.apae.ged.domain.models.Document;
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
        AlunoResponseDTO aluno,
        TipoDocumentoResponseDTO tipoDocumento
) {

    public record AlunoResponseDTO(
            Long id,
            String nome
    ) {
        public static AlunoResponseDTO fromEntity(br.apae.ged.domain.models.Alunos aluno) {
            if (aluno == null) return null;
            return new AlunoResponseDTO(aluno.getId(), aluno.getNome());
        }
    }

    public record TipoDocumentoResponseDTO(
            Long id,
            String nome
    ) {
        public static TipoDocumentoResponseDTO fromEntity(br.apae.ged.domain.models.TipoDocumento tipo) {
            if (tipo == null) return null;
            return new TipoDocumentoResponseDTO(tipo.getId(), tipo.getNome());
        }
    }

    public static DocumentResponseDTO fromEntity(Document document) {
        return new DocumentResponseDTO(
                document.getId(),
                document.getTitulo(),
                document.getTipoConteudo(),
                document.getConteudo(),
                document.getDataUpload(),
                document.getDataDownload(),
                document.getDataDocumento(),
                AlunoResponseDTO.fromEntity(document.getAluno()),
                TipoDocumentoResponseDTO.fromEntity(document.getTipoDocumento())
        );
    }

    public static DocumentResponseDTO fromEntityWithoutContent(Document document) {
        return new DocumentResponseDTO(
                document.getId(),
                document.getTitulo(),
                document.getTipoConteudo(),
                null,
                document.getDataUpload(),
                document.getDataDownload(),
                document.getDataDocumento(),
                AlunoResponseDTO.fromEntity(document.getAluno()),
                TipoDocumentoResponseDTO.fromEntity(document.getTipoDocumento())
        );
    }
}