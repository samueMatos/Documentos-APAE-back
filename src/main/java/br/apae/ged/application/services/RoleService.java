package br.apae.ged.application.services;

import br.apae.ged.domain.models.Roles;
import br.apae.ged.domain.repositories.RolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RolesRepository rolesRepository;

    public Roles create(Roles roles){
        return rolesRepository.save(roles);
    }

    public Roles retrieve(String nome){
        return rolesRepository.findByNome(nome);
    }
}
