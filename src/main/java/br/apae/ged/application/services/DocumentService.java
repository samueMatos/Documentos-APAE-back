package br.apae.ged.application.services;

import br.apae.ged.application.dto.document.DocumentRequestDTO;
import br.apae.ged.application.dto.document.DocumentResponseDTO;
import br.apae.ged.application.dto.document.DocumentUploadResponseDTO;
import br.apae.ged.application.exceptions.NotFoundException;
import br.apae.ged.application.exceptions.ValidationException;
import br.apae.ged.domain.models.Document;
import br.apae.ged.domain.models.Pessoa;
import br.apae.ged.domain.models.TipoDocumento;
import br.apae.ged.domain.repositories.DocumentRepository;
import br.apae.ged.domain.repositories.PessoaRepository;
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
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PessoaRepository pessoaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public DocumentUploadResponseDTO save(DocumentRequestDTO dto, Long pessoaId) throws IOException {
        MultipartFile arquivo = dto.file();
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ValidationException("O arquivo está vazio");
        }
        if (dto.dataDocumento() == null) {
            throw new ValidationException("A data do documento é obrigatória.");
        }

        Pessoa pessoa = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new NotFoundException("Pessoa (Aluno ou Colaborador) não encontrada."));

        var user = AuthenticationUtil.retriveAuthenticatedUser();
        TipoDocumento tipoDoc = tipoDocumentoRepository.findByNome(dto.tipoDocumento())
                .orElseThrow(() -> new NotFoundException(
                        "Tipo de Documento com o nome '" + dto.tipoDocumento() + "' não encontrado."));

        LocalDate dataDoDocumento = dto.dataDocumento();
        String dataFormatada = dataDoDocumento.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String novoTitulo = String.format("%s - %s - %s", pessoa.getNome(), tipoDoc.getNome(), dataFormatada);

        String conteudoEmBase64 = Base64.getEncoder().encodeToString(arquivo.getBytes());
        String tipoDoConteudo = arquivo.getContentType();

        Document novoDocumento = Document.builder()
                .titulo(novoTitulo)
                .tipoDocumento(tipoDoc)
                .pessoa(pessoa)
                .uploadedBy(user)
                .dataUpload(LocalDateTime.now())
                .conteudo(conteudoEmBase64)
                .tipoConteudo(tipoDoConteudo)
                .isAtivo(true)
                .isLast(true)
                .dataDocumento(dto.dataDocumento())
                .build();

        var documentoSalvo = documentRepository.save(novoDocumento);
        return new DocumentUploadResponseDTO(
                documentoSalvo.getId(),
                "Upload de documento efetuado com sucesso!");
    }

    public Page<DocumentResponseDTO> visualizarTodos(String termoBusca, Pageable pageable) {
        Specification<Document> specFinal = Specification.where(DocumentSpecification.isLast())
                .and(DocumentSpecification.isAtivo());

        if (termoBusca != null && !termoBusca.isBlank()) {
            specFinal = specFinal.and(DocumentSpecification.byTermoBusca(termoBusca));
        }

        Pageable pageableComOrdenacao = pageable;
        if (pageable.getSort().isUnsorted()) {
            pageableComOrdenacao = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("dataUpload").descending());
        }
        Page<Document> documentPage = documentRepository.findAll(specFinal, pageableComOrdenacao);

        return documentPage.map(DocumentResponseDTO::fromEntityWithoutContent);
    }

    public DocumentResponseDTO visualizarUm(Long id) {
        var documento = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Não foi possível encontrar o documento"));
        byte[] imagem = Base64.getDecoder().decode(documento.getConteudo());
        return DocumentResponseDTO.fromEntity(documento, imagem);
    }

    public DocumentResponseDTO update(Long id, DocumentRequestDTO dto) throws IOException {
        Document documentoParaAtualizar = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Documento com ID " + id + " não encontrado."));

        if (dto.tipoDocumento() != null && !dto.tipoDocumento().isBlank()) {
            TipoDocumento tipoDoc = tipoDocumentoRepository.findByNome(dto.tipoDocumento())
                    .orElseThrow(() -> new NotFoundException(
                            "Tipo de Documento com o nome '" + dto.tipoDocumento() + "' não encontrado."));
            documentoParaAtualizar.setTipoDocumento(tipoDoc);
        }

        MultipartFile arquivo = dto.file();
        if (arquivo != null && !arquivo.isEmpty()) {
            String conteudoEmBase64 = Base64.getEncoder().encodeToString(arquivo.getBytes());
            documentoParaAtualizar.setConteudo(conteudoEmBase64);
            documentoParaAtualizar.setTipoConteudo(arquivo.getContentType());
        }

        if (dto.dataDocumento() != null) {
            documentoParaAtualizar.setDataDocumento(dto.dataDocumento());
        }

        Document documentoAtualizado = documentRepository.save(documentoParaAtualizar);
        return DocumentResponseDTO.fromEntityWithoutContent(documentoAtualizado);
    }

    public void changeStatus(Long id) {
        Document documento = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Documento com ID " + id + " não encontrado."));

        documento.setAtivo(!documento.isAtivo());
        documento.setUpdatedBy(AuthenticationUtil.retriveAuthenticatedUser());
        documento.setDataUpdate(LocalDateTime.now());

        documentRepository.save(documento);
    }
}