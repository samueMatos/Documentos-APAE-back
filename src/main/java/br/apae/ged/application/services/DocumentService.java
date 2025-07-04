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

//    public DocumentUploadResponseDTO save(DocumentRequestDTO document, Long alunoID) throws IOException {
//
//        if (document.prevVersion() != null) {
//
//            if (!Objects.equals(document.prevVersion().getAluno().getId(), alunoID)){
//                throw new ValidationException("O aluno vinculado a este novo documento não é o mesmo da versão anterior");
//            }
//
//            var last = documentRepository.findById(document.prevVersion().getId());
//            if (last.isPresent()) {
//                var updated = last.get();
//                updated.setIsLast(false);
//                documentRepository.save(updated);
//            }
//        }
//
//        Alunos aluno = alunoRepository.findById(alunoID).orElseThrow(() -> new NotFoundException("Aluno não encontrado"));
//
//        String path = MultipartFileConverter.convertToFile(document.file(),
//                document.nome().concat("-" + UUID.randomUUID()).concat(".").concat(document.tipoArquivo().toString())).getPath();
//
//        Document doc = Document.builder()
//                .titulo(document.nome())
//                .tipoDocumento(document.tipoDocumento())
//                .tipoArquivo(document.tipoArquivo())
//                .uploadedBy(AuthenticationUtil.retriveAuthenticatedUser())
//                .path(path)
//                .isLast(true)
//                .aluno(aluno)
//                .dataUpload(LocalDateTime.now())
//                .prevVersion(document.prevVersion() == null ? null : documentRepository.findById(document.prevVersion().getId())
//                        .orElseThrow(() -> new NotFoundException("Documento atribuído como versão anterior não existe")))
//                .build();
//
//        documentRepository.save(doc);
//
//        return new DocumentUploadResponseDTO(201, "Upload de documento efetuado com sucesso!");
//    }
//
//    public List<DocumentResponseDTO> list(Long alunoID, String titulo, String alunoNome) {
//
//        var spec = Specification
//                .where(DocumentSpecification.isLast())
//                .and(DocumentSpecification.dateDesc())
//                .and(DocumentSpecification.byAlunoId(alunoID)
//                        .and(DocumentSpecification.byTitulo(titulo))
//                        .and(DocumentSpecification.byAlunoNome(alunoNome)));
//
//        return documentRepository.findAll(spec)
//                .stream()
//                .map(DocumentResponseDTO::fromEntity)
//                .toList();
//    }
//
//    public List<DocumentResponseDTO> byID(Long id) {
//        List<DocumentResponseDTO> resp = new ArrayList<>();
//        getDocumentsRecursively(documentRepository.findById(id).orElseThrow(()
//                -> new NotFoundException("documento não encontrado")), resp);
//
//        return resp;
//    }
//
//
//    public Resource downloadFile(Long id) throws MalformedURLException {
//
//        Document doc = documentRepository.findById(id).orElseThrow(() -> new NotFoundException("Documento não existe"));
//
//        Path filePath = Paths.get(doc.getPath()).normalize();
//
//        Resource resource = new UrlResource(filePath.toUri());
//
//        if (!resource.exists() || !resource.isReadable()){
//            throw new NotFoundException("Não foi possível baixar o arquivo");
//        }
//
//        doc.setDataDownload(LocalDateTime.now());
//        doc.setDownloadedBy(AuthenticationUtil.retriveAuthenticatedUser());
//
//        documentRepository.save(doc);
//
//        return resource;
//    }
//
//    private void getDocumentsRecursively(Document document, List<DocumentResponseDTO> resp){
//        if (resp.size() == 5) return;
//        resp.add(DocumentResponseDTO.fromEntity(document));
//        if (document.getPrevVersion() == null) return;
//        var prev = document.getPrevVersion();
//        getDocumentsRecursively(prev, resp);
//    }
}