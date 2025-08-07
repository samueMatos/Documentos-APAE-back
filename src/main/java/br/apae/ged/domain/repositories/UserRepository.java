package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM tb_users u JOIN FETCH u.userGroup")
    Page<User> findAllWithGroups(Pageable pageable);

    @Query("SELECT u FROM tb_users u JOIN FETCH u.userGroup WHERE " +
            "LOWER(u.nome) LIKE LOWER(CONCAT('%', :termoBusca, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :termoBusca, '%'))")
    Page<User> findByTermoBusca(@Param("termoBusca") String termoBusca, Pageable pageable);
}