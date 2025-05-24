package br.apae.ged.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.apae.ged.application.dto.tipoDocumento.TipoDocumentoRequest;
import br.apae.ged.application.dto.tipoDocumento.TipoDocumentoResponse;
import br.apae.ged.domain.repositories.TipoDocumentoRepository;

@Service
public class TipoDocumentoService {
    
    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    public TipoDocumentoResponse save(TipoDocumentoRequest tipoDocumentoRequest) {
        
    }

}
