package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {}
