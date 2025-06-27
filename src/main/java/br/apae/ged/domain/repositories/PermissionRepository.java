package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {}
