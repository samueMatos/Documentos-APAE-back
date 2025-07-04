package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    boolean existsByEmail(String email);
    Page<User> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    @Query("SELECT u FROM tb_users u JOIN FETCH u.userGroup")
    Page<User> findAllWithGroups(Pageable pageable);

    @Query("SELECT u FROM tb_users u JOIN FETCH u.userGroup WHERE lower(u.nome) LIKE lower(concat('%', :nome, '%'))")
    Page<User> findByNomeContainingIgnoreCaseWithGroups(String nome, Pageable pageable);
}
