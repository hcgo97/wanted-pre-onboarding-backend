package com.example.wpob.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class UserTokenDto implements UserDetails {

    private String accessToken;
    private UserInfoDto userInfoDto;

    @Builder
    public UserTokenDto(String accessToken, Long userId, String email) {
        this.accessToken = accessToken;
        this.userInfoDto = UserInfoDto.builder()
                .id(userId)
                .email(email)
                .build();
    }

    public Long getUserId() {
        // SecurityContextHolder.getContext().getAuthentication().getPrincipal()에서 사용
        return this.userInfoDto.getId();
    }

    @Override
    public String getUsername() {
        return this.userInfoDto.getEmail();
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
