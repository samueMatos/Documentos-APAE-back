package br.apae.ged.presentation.controllers;

import br.apae.ged.domain.models.UserGroup;
import br.apae.ged.application.services.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grupo_usuario")
@PreAuthorize("hasAuthority('GRUPOS_PERMISSOES')")
public class UserGroupController {

    @Autowired
    private UserGroupService userGroupService;


    @GetMapping("/list")
    public List<UserGroup> getAllGroups() {
        return userGroupService.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserGroup> getGroupById(@PathVariable Long id) {
        UserGroup userGroup = userGroupService.findById(id);
        return ResponseEntity.ok(userGroup);
    }


    @PostMapping("/create")
    public ResponseEntity<UserGroup> createGroup(@RequestBody UserGroup userGroup) {
        UserGroup newGroup = userGroupService.create(userGroup);
        return new ResponseEntity<>(newGroup, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserGroup> updateGroup(@PathVariable Long id, @RequestBody UserGroup groupDetails) {
        UserGroup updatedGroup = userGroupService.update(id, groupDetails);
        return ResponseEntity.ok(updatedGroup);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        userGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}