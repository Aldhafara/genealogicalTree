package com.aldhafara.genealogicalTree.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
public class UserDto implements UserDetails {
    private UUID id;
    @Setter
    private String login;
    @Setter
    private String password;
    @Setter
    private String roles;
    @Setter
    private UUID detailsId;

    public UserDto(Builder builder) {
        this.id = builder.id;
        this.login = builder.login;
        this.password = builder.password;
        this.roles = builder.roles;
        this.detailsId = builder.detailsId;
    }

    public UserDto() {
    }

    public static Builder builder() {
        return new UserDto.Builder();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return login;
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

    public boolean hasRole(String role) {
        return roles.toUpperCase().contains(role.toUpperCase());
    }

    public static final class Builder {
        private UUID id;
        private String login;
        private String password;
        private String roles;
        private UUID detailsId;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder login(String login) {
            this.login = login;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder roles(String roles) {
            this.roles = roles;
            return this;
        }

        public Builder detailsId(UUID detailsId) {
            this.detailsId = detailsId;
            return this;
        }

        public UserDto build() {
            return new UserDto(this);
        }
    }
}
