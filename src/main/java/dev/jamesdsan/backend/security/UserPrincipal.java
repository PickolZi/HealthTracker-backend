package dev.jamesdsan.backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final long id;
    private final String email;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(long id, String email, String username, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.authorities = authorities;
    }

    public UserPrincipal(long id, String email, String username, List<String> authorities) {
        this.id = id;
        this.email = email;
        this.username = username;

        this.authorities = authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority))
                .toList();

    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // not needed with JWT
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
        return true;
    }

    @Override
    public String toString() {
        return String.format("UserPrincipal=[id=%d, email=%s, username=%s, roles=%s]", this.getId(), this.getEmail(),
                this.getUsername(), this.getAuthorities());
    }
}
