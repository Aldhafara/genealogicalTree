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

    public UUID getCurrentUserId() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        UserDto user = (UserDto) authentication.getPrincipal();
        return (user != null) ? user.getId() : null;
    }
}
