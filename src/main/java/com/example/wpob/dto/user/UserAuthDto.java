package com.example.wpob.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class UserAuthDto implements UserDetails {

    private String token;
    private String userId; // users PK
    private String email; // login id

    @Builder
    public UserAuthDto(String token, String userId, String email) {
        this.token = token;
        this.userId = userId;
        this.email = email;
    }

    @Override
    public String getUsername() {
        // SecurityContextHolder.getContext().getAuthentication().getPrincipal()에서 사용
        return this.userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
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
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
