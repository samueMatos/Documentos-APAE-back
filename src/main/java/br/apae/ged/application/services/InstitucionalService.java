package br.apae.ged.application.services;

import br.apae.ged.application.dto.document.DocumentUploadResponseDTO;
import br.apae.ged.application.dto.documentoIstitucional.*;
import br.apae.ged.application.exceptions.NotFoundException;
import br.apae.ged.application.exceptions.ValidationException;
import br.apae.ged.domain.models.Institucional;
import br.apae.ged.domain.models.TipoDocumento;
import br.apae.ged.domain.repositories.InstitucionalRepository;
import br.apae.ged.domain.repositories.TipoDocumentoRepository;
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
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstitucionalService {

    private final InstitucionalRepository institucionalRepository;

    private final TipoDocumentoRepository tipoDocumentoRepository;

    public byte[] gerarPdf (GerarDocInstitucionalRequest entrada) throws Exception {
        try ( PDDocument documento = new PDDocument();
              ByteArrayOutputStream saida = new ByteArrayOutputStream()){
            PDPage page = new PDPage();
            documento.addPage(page);

            PDType0Font fontBold = loadFont(documento, "/fonts/Roboto-Bold.ttf");
            PDType0Font fontRegular = loadFont(documento, "/fonts/Roboto-Regular.ttf");

            float margin = 50;
            float yPosition = page.getMediaBox().getHeight() - margin;

            try (PDPageContentStream contentStream = new PDPageContentStream(documento, page)) {
                contentStream.beginText();
                contentStream.setFont(fontBold, 16);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(entrada.titulo() != null ? entrada.titulo() : "Título Padrão");
                contentStream.endText();
                yPosition -= 40;

                contentStream.beginText();
                contentStream.setFont(fontRegular, 12);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(margin, yPosition);

                String corpoDoTexto = entrada.texto();
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
                contentStream.showText(entrada.rodape() != null ? entrada.rodape() : "");
                contentStream.endText();
            }
            documento.save(saida);
            return saida.toByteArray();
        } catch (Exception e){
            throw new Exception("Não foi possível criar o PDF");
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

    public InstucionalUploadResponse uploadDocumento (UploadInstitucionalRequest entrada) throws IOException {
        MultipartFile documento = entrada.documento();
        if (documento == null || documento.isEmpty()){
            throw new ValidationException("O arquivo está vazio");
        }
        if (entrada.dataCriacao() == null) {
            throw new ValidationException("Data do documento é obrigatória");
        }

        var usuario = AuthenticationUtil.retriveAuthenticatedUser();

        String tipoDocumentoNome = entrada.tipoDocumento().trim();
        TipoDocumento tipoDoc = tipoDocumentoRepository.findByNomeIgnoreCase(tipoDocumentoNome)
                .orElseThrow(() -> new NotFoundException("Tipo de documento '" + tipoDocumentoNome + "' não encontrado"));

        String base64 = Base64.getEncoder().encodeToString(documento.getBytes());
        String tConteudo = documento.getContentType();

        institucionalRepository.save( new Institucional(entrada, base64, tConteudo, tipoDoc, usuario));
        return new InstucionalUploadResponse(entrada.nome());
    }

    public DocumentUploadResponseDTO gerarESalvarPdf(GerarDocInstitucionalRequest dto) throws Exception {
        byte[] pdfBytes = gerarPdf(dto);
        String base64 = Base64.getEncoder().encodeToString(pdfBytes);

        var usuario = AuthenticationUtil.retriveAuthenticatedUser();

        // Busca o TipoDocumento, que é essencial para a persistência.
        TipoDocumento tipoDoc = tipoDocumentoRepository.findByNomeIgnoreCase(dto.tipoDocumento().trim())
                .orElseThrow(() -> new NotFoundException("Tipo de documento '" + dto.tipoDocumento() + "' não encontrado"));

        Institucional institucional = new Institucional();
        institucional.setTitulo(dto.titulo() != null ? dto.titulo() : "Documento Sem Título");
        institucional.setConteudo(base64);
        institucional.setTipoConteudo("application/pdf");
        institucional.setDataDocumento(LocalDate.now());
        institucional.setDataUpload(LocalDateTime.now());
        institucional.setAtivo(true);
        institucional.setUploadedBy(usuario);
        institucional.setTipoDocumento(tipoDoc);
        
        Institucional docSalvo = institucionalRepository.save(institucional);

        return new DocumentUploadResponseDTO(docSalvo.getId(), "Documento institucional gerado e salvo com sucesso!");
    }

    public Page<InstucionalResponse> listarDocumentos(String tipoDocumento, String titulo, LocalDate dataCriacao, Pageable pageable) {
        Specification<Institucional> spec = (root, query, cb) -> cb.isTrue(root.get("isAtivo"));

        if (tipoDocumento != null && !tipoDocumento.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("tipoDocumento").get("nome")), "%" + tipoDocumento.toLowerCase() + "%")
            );
        }

        if (titulo != null && !titulo.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("titulo")), "%" + titulo.toLowerCase() + "%")
            );
        }

        if (dataCriacao != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("dataDocumento"), dataCriacao)
            );
        }

        Pageable pageableComOrdenacao = pageable;
        if (pageable.getSort().isUnsorted()) {
            pageableComOrdenacao = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("dataUpload").descending()
            );
        }

        Page<Institucional> documentPage = institucionalRepository.findAll(spec, pageableComOrdenacao);
        return documentPage.map(InstucionalResponse::new);
    }

    public InstucionalResponse visualizarUm(Long id) {
        var institucional = institucionalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Não foi possível encontrar o documento institucional"));

        byte[] conteudo = Base64.getDecoder().decode(institucional.getConteudo());
        return new InstucionalResponse(institucional);
    }

    public InstucionalResponse atualizar (Long id, AtualizarInstitucionalRequest dto) throws IOException {
        var institucional = institucionalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Não foi possível encontrar o documento institucional"));
        if (dto.tipoDocumento() != null && !dto.tipoDocumento().isBlank()) {
            TipoDocumento tipoDoc = tipoDocumentoRepository.findByNomeIgnoreCase(dto.tipoDocumento().trim())
                    .orElseThrow(() -> new NotFoundException("Tipo de documento não encontrado"));
            institucional.setTipoDocumento(tipoDoc);
        }
        if (dto.nome() != null && !dto.nome().isEmpty()) {
            institucional.setTitulo(dto.nome());
        }
        if (dto.dataCriacao() != null) {
            institucional.setDataDocumento(dto.dataCriacao());
        }

        if (dto.file() != null && !dto.file().isEmpty()) {
            MultipartFile documento = dto.file();
            String base64 = Base64.getEncoder().encodeToString(documento.getBytes());
            String tConteudo = documento.getContentType();
            institucional.setConteudo(base64);
            institucional.setTipoConteudo(tConteudo);
        }

        institucional.setUpdatedBy(AuthenticationUtil.retriveAuthenticatedUser());
        institucional.setDataUpdate(LocalDateTime.now());
        Institucional docAtualizado = institucionalRepository.save(institucional);
        return new InstucionalResponse(docAtualizado);
    }

    public void inativar (Long id) {
        var institucional = institucionalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Não foi possível encontrar o documento institucional"));
        institucional.setAtivo(false);
        institucional.setUpdatedBy(AuthenticationUtil.retriveAuthenticatedUser());
        institucional.setDataUpdate(LocalDateTime.now());
        institucionalRepository.save(institucional);
    }
}
