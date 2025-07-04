package br.apae.ged.application.dto.document;

import br.apae.ged.domain.models.Document;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * DTO para respostas de Documentos.
 */
public record DocumentResponseDTO(
        Long id,
        String nome,
        String tipoConteudo,
        String documento, // Representa o conteúdo em Base64 como uma String
        LocalDateTime dataUpload,
        LocalDateTime dataDownload,
        AlunoResponseDTO aluno, // 1. Campo do aluno ADICIONADO
        TipoDocumentoResponseDTO tipoDocumento // 2. Usando um DTO aninhado
) {

    /**
     * DTO aninhado para representar o Aluno de forma simples.
     */
    public record AlunoResponseDTO(
            Long id,
            String nome
    ) {
        public static AlunoResponseDTO fromEntity(br.apae.ged.domain.models.Alunos aluno) {
            if (aluno == null) return null;
            return new AlunoResponseDTO(aluno.getId(), aluno.getNome());
        }
    }

    /**
     * DTO aninhado para representar o TipoDocumento de forma simples.
     */
    public record TipoDocumentoResponseDTO(
            Long id,
            String nome
    ) {
        public static TipoDocumentoResponseDTO fromEntity(br.apae.ged.domain.models.TipoDocumento tipo) {
            if (tipo == null) return null;
            return new TipoDocumentoResponseDTO(tipo.getId(), tipo.getNome());
        }
    }

    /**
     * Cria o DTO a partir da Entidade, INCLUINDO o conteúdo do arquivo.
     * Usado para visualizar um único documento.
     */
    public static DocumentResponseDTO fromEntity(Document document, byte[] documento) {
        // 3. Converte o array de bytes para uma String Base64
        String base64Content = (documento != null) ? Base64.getEncoder().encodeToString(documento) : null;

        return new DocumentResponseDTO(
                document.getId(),
                document.getTitulo(),
                document.getTipoConteudo(),
                base64Content,
                document.getDataUpload(),
                document.getDataDownload(),
                AlunoResponseDTO.fromEntity(document.getAluno()),
                TipoDocumentoResponseDTO.fromEntity(document.getTipoDocumento())
        );
    }

    /**
     * Cria o DTO a partir da Entidade, SEM o conteúdo do arquivo.
     * Usado para listagens, para não transferir dados pesados.
     */
    public static DocumentResponseDTO fromEntityWithoutContent(Document document) {
        return new DocumentResponseDTO(
                document.getId(),
                document.getTitulo(),
                document.getTipoConteudo(),
                null, // Conteúdo não é enviado na listagem
                document.getDataUpload(),
                document.getDataDownload(),
                AlunoResponseDTO.fromEntity(document.getAluno()),
                TipoDocumentoResponseDTO.fromEntity(document.getTipoDocumento())
        );
    }
}