package br.apae.ged.application.services;

import br.apae.ged.domain.models.Permission;
import br.apae.ged.domain.models.UserGroup;
import br.apae.ged.domain.repositories.PermissionRepository;
import br.apae.ged.domain.repositories.UserGroupRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserGroupService {

    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private PermissionRepository permissionRepository;


    @Transactional
    public List<UserGroup> findAll() {
        return userGroupRepository.findAll();
    }

    @Transactional
    public UserGroup findById(Long id) {
        return userGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado com o ID: " + id));
    }

    @Transactional
    public UserGroup create(UserGroup userGroup) {
        return userGroupRepository.save(userGroup);
    }

    @Transactional
    public UserGroup update(Long id, UserGroup groupDetails) {
        UserGroup existingGroup = userGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado com o ID: " + id));


        existingGroup.setNome(groupDetails.getNome());

        List<Permission> updatedPermissions = new ArrayList<>();
        if (groupDetails.getPermissions() != null && !groupDetails.getPermissions().isEmpty()) {

            List<Long> permissionIds = groupDetails.getPermissions().stream()
                    .map(Permission::getId)
                    .collect(Collectors.toList());


            updatedPermissions = permissionRepository.findAllById(permissionIds);


            if (updatedPermissions.size() != permissionIds.size()) {
                throw new EntityNotFoundException("Uma ou mais permissões não foram encontradas.");
            }
        }


        existingGroup.setPermissions(updatedPermissions);


        return userGroupRepository.save(existingGroup);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        userGroupRepository.deleteById(id);
    }
}