package com.tickets.authservice.security;

import com.tickets.authservice.entity.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class UsuarioPrincipal implements UserDetails {
    private Integer idUsuario;
    private String email;
    private String password;
    private String nombres;
    private boolean estado;
    private Collection<? extends GrantedAuthority> authorities;

    public static UsuarioPrincipal build(Usuario usuario) {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre())
        );
        return new UsuarioPrincipal(
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                usuario.getPassword(),
                usuario.getNombres(),
                usuario.isEstado(),
                authorities
        );
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombres() {
        return nombres;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return estado;
    }
}