package com.aldhafara.genealogicalTree.configuration;

import com.aldhafara.genealogicalTree.models.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityContextFacade {

    public SecurityContext getCurrentSecurityContext() {
        return SecurityContextHolder.getContext();
    }

    public Authentication getAuthentication() {
        return getCurrentSecurityContext().getAuthentication();
    }

    private UserDto getCurrentUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        return (UserDto) authentication.getPrincipal();
    }

    public UUID getCurrentUserId() {
        UserDto user = getCurrentUser();
        return (user != null) ? user.getId() : null;
    }

    public UUID getCurrentUserDetailsId() {
        UserDto user = getCurrentUser();
        return (user != null) ? user.getDetailsId() : null;
    }
}
