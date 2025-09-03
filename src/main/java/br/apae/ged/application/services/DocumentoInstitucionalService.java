package br.apae.ged.application.services;

import br.apae.ged.application.dto.documento_institucional.DocumentoInstitucionalRequestDTO;
import br.apae.ged.application.dto.documento_institucional.DocumentoInstitucionalResponseDTO;
import br.apae.ged.application.exceptions.NotFoundException;
import br.apae.ged.application.exceptions.ValidationException;
import br.apae.ged.domain.models.DocumentoInstitucional;
import br.apae.ged.domain.repositories.DocumentoInstitucionalRepository;
import br.apae.ged.domain.utils.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class DocumentoInstitucionalService {

    private final DocumentoInstitucionalRepository repository;

    public DocumentoInstitucionalResponseDTO save(DocumentoInstitucionalRequestDTO dto) throws IOException {
        MultipartFile arquivo = dto.file();
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ValidationException("O arquivo está vazio.");
        }
        if (dto.titulo() == null || dto.titulo().isBlank()) {
            throw new ValidationException("O título do documento é obrigatório.");
        }

        String conteudoEmBase64 = Base64.getEncoder().encodeToString(arquivo.getBytes());

        DocumentoInstitucional novoDocumento = DocumentoInstitucional.builder()
                .titulo(dto.titulo())
                .conteudo(conteudoEmBase64)
                .tipoConteudo(arquivo.getContentType())
                .uploadedBy(AuthenticationUtil.retriveAuthenticatedUser())
                .build();

        var documentoSalvo = repository.save(novoDocumento);
        return DocumentoInstitucionalResponseDTO.fromEntity(documentoSalvo);
    }

    public Page<DocumentoInstitucionalResponseDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(DocumentoInstitucionalResponseDTO::fromEntity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Documento institucional não encontrado com o id: " + id);
        }
        repository.deleteById(id);
    }
}