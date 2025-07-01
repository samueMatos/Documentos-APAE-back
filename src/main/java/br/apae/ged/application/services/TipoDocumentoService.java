package br.apae.ged.application.services;

import br.apae.ged.application.dto.tipoDocumento.TipoDocumentoRequest;
import br.apae.ged.application.dto.tipoDocumento.TipoDocumentoResponse;
import br.apae.ged.application.exceptions.NotFoundException;
import br.apae.ged.domain.models.TipoDocumento;
import br.apae.ged.domain.repositories.TipoDocumentoRepository;
import br.apae.ged.domain.utils.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoDocumentoService {

    private final TipoDocumentoRepository tipoDocumentoRepository;

    public TipoDocumentoResponse create(TipoDocumentoRequest request) {
        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setNome(request.nome());
        tipoDocumento.setValidadeEmDias(request.validadeEmDias());
        tipoDocumento.setUsuario(AuthenticationUtil.retriveAuthenticatedUser());
        tipoDocumento.setDataRegistro(LocalDateTime.now());

        TipoDocumento savedEntity = tipoDocumentoRepository.save(tipoDocumento);
        return new TipoDocumentoResponse(savedEntity);
    }

    public List<TipoDocumentoResponse> findAll() {
        return tipoDocumentoRepository.findAll().stream()
                .map(TipoDocumentoResponse::new)
                .collect(Collectors.toList());
    }

    public TipoDocumentoResponse findById(Long id) {
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tipo de Documento não encontrado com o id: " + id));
        return new TipoDocumentoResponse(tipoDocumento);
    }

    public TipoDocumentoResponse update(Long id, TipoDocumentoRequest request) {
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tipo de Documento não encontrado com o id: " + id));

        tipoDocumento.setNome(request.nome());
        tipoDocumento.setValidadeEmDias(request.validadeEmDias());
        tipoDocumento.setUsuarioAlteracao(AuthenticationUtil.retriveAuthenticatedUser());
        tipoDocumento.setDataAlteracao(LocalDateTime.now());

        TipoDocumento updatedEntity = tipoDocumentoRepository.save(tipoDocumento);
        return new TipoDocumentoResponse(updatedEntity);
    }

    public void delete(Long id) {
        if (!tipoDocumentoRepository.existsById(id)) {
            throw new NotFoundException("Tipo de Documento não encontrado com o id: " + id);
        }
        tipoDocumentoRepository.deleteById(id);
    }
}