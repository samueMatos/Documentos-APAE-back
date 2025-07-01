package br.apae.ged.application.services;

import br.apae.ged.domain.models.Permission;
import br.apae.ged.domain.repositories.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Transactional
    public Permission create(Permission permission) {

        Optional<Permission> existingPermission = permissionRepository.findByNome(permission.getNome());
        if (existingPermission.isPresent()) {
            throw new IllegalArgumentException("Uma permissão com o nome '" + permission.getNome() + "' já existe.");
        }
        return permissionRepository.save(permission);
    }

    @Transactional
    public void delete(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new EntityNotFoundException("Permissão não encontrada com o ID: " + id);
        }
        permissionRepository.deleteById(id);
    }
}