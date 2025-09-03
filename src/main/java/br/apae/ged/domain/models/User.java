package br.apae.ged.domain.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
@Entity(name = "tb_users")
@Table(indexes = {
        @Index(name = "email_idx", columnList = "email")
})
public class User extends EntityID implements UserDetails{

    private String email;
    private String nome;
    private String password;
    private Boolean isAtivo;
    private String recoveryCode;
    private LocalDateTime recoveryCodeExpiration;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup userGroup;

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public User(String nome, String email, String password){
        this.nome = nome;
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userGroup == null || userGroup.getPermissions() == null) return List.of();
        return userGroup.getPermissions();
    }
}