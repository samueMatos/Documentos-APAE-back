package br.apae.ged.application.services;

import br.apae.ged.application.dto.colaborador.ColaboradorRequestDTO;
import br.apae.ged.application.dto.colaborador.ColaboradorResponseDTO;
import br.apae.ged.application.exceptions.BusinessException;
import br.apae.ged.application.exceptions.NotFoundException;
import br.apae.ged.domain.models.Colaborador;
import br.apae.ged.domain.repositories.ColaboradorRepository;
import br.apae.ged.domain.utils.AuthenticationUtil;
import br.apae.ged.domain.valueObjects.CPF;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;

    public ColaboradorResponseDTO create(ColaboradorRequestDTO request) {
        CPF cpf = new CPF(request.cpf());
        Optional<Colaborador> colaboradorExistenteOpt = colaboradorRepository.findByCpf(cpf);

        if (colaboradorExistenteOpt.isPresent()) {
            Colaborador colaboradorExistente = colaboradorExistenteOpt.get();
            if (colaboradorExistente.getIsAtivo()) {
                throw new BusinessException("Já existe um colaborador ativo cadastrado com este CPF.");
            } else {
                // Reativa e atualiza o colaborador inativo
                colaboradorExistente.setNome(request.nome());
                colaboradorExistente.setCargo(request.cargo());
                colaboradorExistente.setIsAtivo(true);
                colaboradorExistente.setUpdatedAt(LocalDateTime.now());
                colaboradorExistente.setUpdatedBy(AuthenticationUtil.retriveAuthenticatedUser());
                Colaborador savedColaborador = colaboradorRepository.save(colaboradorExistente);
                return ColaboradorResponseDTO.fromEntity(savedColaborador);
            }
        }

        Colaborador novoColaborador = new Colaborador();
        novoColaborador.setNome(request.nome());
        novoColaborador.setCpf(cpf);
        novoColaborador.setCargo(request.cargo());
        novoColaborador.setIsAtivo(true);
        novoColaborador.setCreatedBy(AuthenticationUtil.retriveAuthenticatedUser());
        novoColaborador.setCreatedAt(LocalDateTime.now());

        Colaborador savedColaborador = colaboradorRepository.save(novoColaborador);
        return ColaboradorResponseDTO.fromEntity(savedColaborador);
    }

    public Page<ColaboradorResponseDTO> findAll(Pageable pageable) {
        return colaboradorRepository.findAll(pageable).map(ColaboradorResponseDTO::fromEntity);
    }

    public ColaboradorResponseDTO findById(Long id) {
        Colaborador colaborador = colaboradorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Colaborador não encontrado com o id: " + id));
        return ColaboradorResponseDTO.fromEntity(colaborador);
    }

    public ColaboradorResponseDTO update(Long id, ColaboradorRequestDTO request) {
        Colaborador colaborador = colaboradorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Colaborador não encontrado com o id: " + id));

        colaborador.setNome(request.nome());
        colaborador.setCpf(new CPF(request.cpf()));
        colaborador.setCargo(request.cargo());
        colaborador.setUpdatedAt(LocalDateTime.now());
        colaborador.setUpdatedBy(AuthenticationUtil.retriveAuthenticatedUser());

        Colaborador updatedColaborador = colaboradorRepository.save(colaborador);
        return ColaboradorResponseDTO.fromEntity(updatedColaborador);
    }

    public void changeStatus(Long id) {
        Colaborador colaborador = colaboradorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Colaborador não encontrado com o id: " + id));

        colaborador.setIsAtivo(!colaborador.getIsAtivo());
        colaborador.setUpdatedAt(LocalDateTime.now());
        colaborador.setUpdatedBy(AuthenticationUtil.retriveAuthenticatedUser());
        colaboradorRepository.save(colaborador);
    }
}