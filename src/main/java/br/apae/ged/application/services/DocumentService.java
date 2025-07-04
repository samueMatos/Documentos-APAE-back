package br.apae.ged.application.services;

import br.apae.ged.application.dto.document.DocumentRequestDTO;
import br.apae.ged.application.dto.document.DocumentResponseDTO;
import br.apae.ged.application.dto.document.DocumentUploadResponseDTO;
import br.apae.ged.application.exceptions.NotFoundException;
import br.apae.ged.application.exceptions.ValidationException;
import br.apae.ged.domain.models.Alunos;
import br.apae.ged.domain.models.Document;
import br.apae.ged.domain.models.TipoDocumento;
import br.apae.ged.domain.repositories.AlunoRepository;
import br.apae.ged.domain.repositories.DocumentRepository;
import br.apae.ged.domain.repositories.TipoDocumentoRepository;
import br.apae.ged.domain.repositories.specifications.DocumentSpecification;
import br.apae.ged.domain.utils.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final AlunoRepository alunoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public DocumentUploadResponseDTO save(DocumentRequestDTO dto, Long alunoID) throws IOException {
        MultipartFile arquivo = dto.file();
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ValidationException("O arquivo está vazio");
        }
        Alunos aluno = alunoRepository.findById(alunoID)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        var user = AuthenticationUtil.retriveAuthenticatedUser();
        TipoDocumento tipoDoc = tipoDocumentoRepository.findByNome(dto.tipoDocumento())
                .orElseThrow(() -> new NotFoundException("Tipo de Documento com o nome '" + dto.tipoDocumento() + "' não encontrado."));

        String dataFormatada = LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")
        );
        String conteudoEmBase64 = Base64.getEncoder().encodeToString(arquivo.getBytes());
        String tipoDoConteudo = arquivo.getContentType();
        String tituloPadronizado = String.format("%s - %s",
                aluno.getNome(),
                dataFormatada
        );
        Document novoDocumento = Document.builder()
                .titulo(tituloPadronizado)
                .tipoDocumento(tipoDoc)
                .aluno(aluno)
                .uploadedBy(user)
                .dataUpload(LocalDateTime.now())
                .conteudo(conteudoEmBase64)
                .tipoConteudo(tipoDoConteudo)
                .isLast(true)
                .build();

        var documentoSalvo = documentRepository.save(novoDocumento);
        return new DocumentUploadResponseDTO(
                documentoSalvo.getId(),
                "Upload de documento efetuado com sucesso!"
        );
    }

    public Page<DocumentResponseDTO> visualizarTodos(String nome, Pageable pageable) {
        Specification<Document> spec = Specification.where(DocumentSpecification.isLast());
        if (nome != null && !nome.isBlank()) {
            spec = spec.and(DocumentSpecification.byAlunoNome(nome));
        }
        Pageable pageableComOrdenacao = pageable;
        if (pageable.getSort().isUnsorted()) {
            pageableComOrdenacao = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("dataUpload").descending()
            );
        }
        Page<Document> documentPage = documentRepository.findAll(spec, pageableComOrdenacao);

        return documentPage.map(DocumentResponseDTO::fromEntityWithoutContent);
    }

    public DocumentResponseDTO visualizarUm(Long id) {
        var documento = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Não foi possível encontrar o documento"));
        byte[] imagem = Base64.getDecoder().decode(documento.getConteudo());
        return DocumentResponseDTO.fromEntity(documento,imagem );
    }

    public DocumentResponseDTO update(Long id, DocumentRequestDTO dto) throws IOException {
        Document documentoParaAtualizar = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Documento com ID " + id + " não encontrado."));

        if (dto.tipoDocumento() != null && !dto.tipoDocumento().isBlank()) {
            TipoDocumento tipoDoc = tipoDocumentoRepository.findByNome(dto.tipoDocumento())
                    .orElseThrow(() -> new NotFoundException("Tipo de Documento com o nome '" + dto.tipoDocumento() + "' não encontrado."));
            documentoParaAtualizar.setTipoDocumento(tipoDoc);
        }

        MultipartFile arquivo = dto.file();
        if (arquivo != null && !arquivo.isEmpty()) {
            String conteudoEmBase64 = Base64.getEncoder().encodeToString(arquivo.getBytes());
            documentoParaAtualizar.setConteudo(conteudoEmBase64);
            documentoParaAtualizar.setTipoConteudo(arquivo.getContentType());
        }

        Document documentoAtualizado = documentRepository.save(documentoParaAtualizar);
        return DocumentResponseDTO.fromEntityWithoutContent(documentoAtualizado);
    }

    public void delete(Long id) {
        Document documento = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Documento com ID " + id + " não encontrado para exclusão."));
        documentRepository.delete(documento);
    }
}