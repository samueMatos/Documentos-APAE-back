package br.apae.ged.models;

import br.apae.ged.models.enums.TipoRoles;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "tb_roles")
public class Roles implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome;

    @Enumerated(EnumType.STRING)
    private TipoRoles tipoRoles;

    @Override
    public String getAuthority() {
        return nome;
    }
}
