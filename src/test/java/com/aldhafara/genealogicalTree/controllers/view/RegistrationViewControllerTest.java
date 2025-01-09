package com.aldhafara.genealogicalTree.controllers.view;

import com.aldhafara.genealogicalTree.models.SexEnum;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class RegistrationViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRedirectToHomeIfUserIsNotAdmin() throws Exception {
        mockUserWithRole("user", "ROLE_USER");

        mockMvc.perform(get("/register"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void shouldReturnRegisterPageIfUserIsAdmin() throws Exception {
        mockUserWithRole("admin", "ROLE_ADMIN");

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("person"))
                .andExpect(model().attribute("sexOptions", SexEnum.values()));
    }

    @Test
    void shouldAllowAccessToRegisterForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void shouldAllowAccessToRegisterForAuthenticatedAdmin() throws Exception {
        mockUserWithRole("admin", "ROLE_ADMIN");

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void shouldDenyAccessToRegisterForAuthenticatedUserWithoutAdminRole() throws Exception {
        mockUserWithRole("user", "ROLE_USER");

        mockMvc.perform(get("/register"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    private void mockUserWithRole(String username, String role) {
        UserDto user = new UserDto();
        user.setLogin(username);
        user.setPassword("password");
        user.setRoles(role);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "password", user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}