package com.aldhafara.genealogicalTree.configuration;

import com.aldhafara.genealogicalTree.controllers.DefaultController;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DefaultController.class)
@Import(RateLimitingFilter.class)
public class RateLimitingFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Filter rateLimitingFilter;

    @BeforeEach
    void setUp() throws ServletException {
        rateLimitingFilter.init(new MockFilterConfig());
    }

    @Test
    void testRateLimiting() throws Exception {
        mockUserWithRole("user", "ROLE_USER");
        for (int i = 0; i < 100; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()))
                .andExpect(content().string("Too many requests"));
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