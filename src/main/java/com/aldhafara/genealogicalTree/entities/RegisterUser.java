package com.aldhafara.genealogicalTree.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "register_users")
public class RegisterUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID detailsId;
    private String login;
    private String password;
    private String roles;

    public RegisterUser() {
    }

    public RegisterUser(String login, String password, String roles, UUID detailsId) {
        this.login = login;
        this.password = password;
        this.roles = roles;
        this.detailsId = detailsId;
    }

    public RegisterUser(String login, String password, Set<String> roles) {
        this.login = login;
        this.password = password;
        if (!roles.isEmpty()) {
            this.roles = roles.stream().reduce((a, b) -> a + " ; " + b).get();
        }
    }

    public RegisterUser(Builder builder) {
        this.id = builder.id;
        this.login = builder.login;
        this.password = builder.password;
        this.roles = builder.roles;
        this.detailsId = builder.detailsId;
    }

    public static RegisterUser.Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> collection = new HashSet<>();
        for (String role : roles.toUpperCase().split(" ; ")) {
            collection.add(new SimpleGrantedAuthority(role));
        }
        return collection;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public UUID getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(UUID detailsId) {
        this.detailsId = detailsId;
    }

    public static final class Builder {
        private UUID id;
        private String login;
        private String password;
        private String roles;
        private UUID detailsId;

        public RegisterUser.Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public RegisterUser.Builder login(String login) {
            this.login = login;
            return this;
        }

        public RegisterUser.Builder password(String password) {
            this.password = password;
            return this;
        }

        public RegisterUser.Builder roles(String roles) {
            this.roles = roles;
            return this;
        }

        public RegisterUser.Builder details(UUID details) {
            this.detailsId = details;
            return this;
        }

        public RegisterUser build() {
            return new RegisterUser(this);
        }
    }
}
