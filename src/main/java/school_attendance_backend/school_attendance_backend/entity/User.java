package school_attendance_backend.school_attendance_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "t_users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * Retorna as permissões do usuário.
     * O Spring Security usa isso para verificar autorização.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getRole()));
    }

    /**
     * Retorna a senha do usuário
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Retorna o username usado no login
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Conta não expirada
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Conta não bloqueada
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Credenciais válidas
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Conta ativa
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}