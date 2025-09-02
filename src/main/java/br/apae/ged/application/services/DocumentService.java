package br.apae.ged.application.services;

import br.apae.ged.application.dto.document.GerarDocumentoAlunoDTO;
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final AlunoRepository alunoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public byte[] gerarPdf(GerarDocumentoAlunoDTO dto) throws IOException {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDType0Font fontBold = loadFont(document, "/fonts/Roboto-Bold.ttf");
            PDType0Font fontRegular = loadFont(document, "/fonts/Roboto-Regular.ttf");

            float margin = 50;
            float yPosition = page.getMediaBox().getHeight() - margin;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(fontBold, 16);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(dto.textoCabecalho() != null ? dto.textoCabecalho() : "Título Padrão");
                contentStream.endText();
                yPosition -= 40;

                contentStream.beginText();
                contentStream.setFont(fontRegular, 12);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(margin, yPosition);

                String corpoDoTexto = dto.texto();
                if (corpoDoTexto != null && !corpoDoTexto.isEmpty()) {
                    for (String line : corpoDoTexto.split("\n")) {
                        contentStream.showText(line);
                        contentStream.newLine();
                    }
                }
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(fontRegular, 10);
                contentStream.newLineAtOffset(margin, margin);
                contentStream.showText(dto.textoRodape() != null ? dto.textoRodape() : "");
                contentStream.endText();
            }
            document.save(out);
            return out.toByteArray();
        }
    }

    private PDType0Font loadFont(PDDocument document, String path) throws IOException {
        try (InputStream fontStream = DocumentService.class.getResourceAsStream(path)) {
            if (fontStream == null) {
                throw new IOException("Arquivo de fonte não encontrado no classpath: " + path);
            }
            return PDType0Font.load(document, fontStream);
        }
    }

    public DocumentUploadResponseDTO save(DocumentRequestDTO dto, Long alunoID) throws IOException {
        MultipartFile arquivo = dto.file();
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ValidationException("O arquivo está vazio");
        }
        if (dto.dataDocumento() == null) {
            throw new ValidationException("A data do documento é obrigatória.");
        }

        Alunos aluno = alunoRepository.findById(alunoID)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        var user = AuthenticationUtil.retriveAuthenticatedUser();
        TipoDocumento tipoDoc = tipoDocumentoRepository.findByNome(dto.tipoDocumento())
                .orElseThrow(() -> new NotFoundException("Tipo de Documento com o nome '" + dto.tipoDocumento() + "' não encontrado."));

        LocalDate dataDoDocumento = dto.dataDocumento();

        String dataFormatada = dto.dataDocumento().format(
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")
        );
        String novoTitulo = String.format("%s - %s - %s",
                aluno.getNome(),
                tipoDoc.getNome(),
                dataFormatada
        );
        String conteudoEmBase64 = Base64.getEncoder().encodeToString(arquivo.getBytes());
        String tipoDoConteudo = arquivo.getContentType();

        Document novoDocumento = Document.builder()
                .titulo(novoTitulo)
                .tipoDocumento(tipoDoc)
                .aluno(aluno)
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
                "Upload de documento efetuado com sucesso!"
        );
    }

    public DocumentUploadResponseDTO gerarESalvarPdf(GerarDocumentoAlunoDTO dto) throws IOException {
        byte[] pdfBytes = gerarPdf(dto);
        Document documentoSalvo = salvarDocumentoGerado(dto, pdfBytes);
        return new DocumentUploadResponseDTO(documentoSalvo.getId(), "Documento gerado e salvo com sucesso!");
    }
    
    public byte[] gerarSalvarERetornarPdfBytes(GerarDocumentoAlunoDTO dto) throws IOException {
        byte[] pdfBytes = gerarPdf(dto);
        salvarDocumentoGerado(dto, pdfBytes);
        return pdfBytes;
    }

    private Document salvarDocumentoGerado(GerarDocumentoAlunoDTO dto, byte[] pdfBytes) {
        if (dto.alunoId() == null) {
            throw new ValidationException("O ID do aluno é obrigatório.");
        }

        Alunos aluno = alunoRepository.findById(dto.alunoId())
                .orElseThrow(() -> new NotFoundException("Aluno com ID " + dto.alunoId() + " não encontrado."));

        TipoDocumento tipoDoc = tipoDocumentoRepository.findByNome(dto.tipoDocumento())
                .orElseThrow(() -> new NotFoundException("Tipo de Documento com o nome '" + dto.tipoDocumento() + "' não encontrado."));

        var user = AuthenticationUtil.retriveAuthenticatedUser();

        String conteudoEmBase64 = Base64.getEncoder().encodeToString(pdfBytes);
        String tipoDoConteudo = "application/pdf";

        LocalDate dataDoDocumento = LocalDate.now();
        String dataFormatada = dataDoDocumento.format(
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")
        );

        String novoTitulo = String.format("%s - %s - %s",
                aluno.getNome(),
                tipoDoc.getNome(),
                dataFormatada
        );
        Document novoDocumento = Document.builder()
                .titulo(novoTitulo)
                .tipoDocumento(tipoDoc)
                .aluno(aluno)
                .uploadedBy(user)
                .conteudo(conteudoEmBase64)
                .tipoConteudo(tipoDoConteudo)
                .dataDocumento(dataDoDocumento)
                .isLast(true)
                .isAtivo(true)
                .build();

        return documentRepository.save(novoDocumento);
    }

    public Page<DocumentResponseDTO> listarPorAluno(Long alunoId, String termoBusca, Pageable pageable) {
        Specification<Document> specFinal = Specification.where(DocumentSpecification.isLast())
                .and(DocumentSpecification.isAtivo())
                .and((root, query, cb) -> cb.equal(root.get("aluno").get("id"), alunoId));
        if (termoBusca != null && !termoBusca.isBlank()) {
            Specification<Document> specBuscaTitulo = (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), "%" + termoBusca.toLowerCase() + "%");
            specFinal = specFinal.and(specBuscaTitulo);
        }
        Pageable pageableComOrdenacao = pageable;
        if (pageable.getSort().isUnsorted()) {
            pageableComOrdenacao = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("dataUpload").descending()
            );
        }
        Page<Document> documentPage = documentRepository.findAll(specFinal, pageableComOrdenacao);

        return documentPage.map(DocumentResponseDTO::fromEntityWithoutContent);
    }

    public DocumentResponseDTO visualizarUm(Long id) {
        var documento = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Não foi possível encontrar o documento"));
        byte[] imagem = Base64.getDecoder().decode(documento.getConteudo());
        return DocumentResponseDTO.fromEntity(documento );
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