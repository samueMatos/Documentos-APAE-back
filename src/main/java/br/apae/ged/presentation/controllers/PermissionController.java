package br.apae.ged.presentation.controllers;

import br.apae.ged.domain.models.Permission;
import br.apae.ged.application.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/permissoes")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('GRUPOS_PERMISSOES')")
    public List<Permission> getAllPermissions() {
        return permissionService.findAll();
    }



    @PostMapping("/admin/create")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        Permission createdPermission = permissionService.create(permission);
        return new ResponseEntity<>(createdPermission, HttpStatus.CREATED);
    }



    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}