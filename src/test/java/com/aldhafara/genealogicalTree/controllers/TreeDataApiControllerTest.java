package com.aldhafara.genealogicalTree.controllers;

import com.aldhafara.genealogicalTree.models.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TreeDataApiControllerTest {

    private static final String GET_MY_ID_ENDPOINT = "/api/family-tree/get-my-id";
    private static final String GET_STRUCTURE_ENDPOINT = "/api/family-tree/get-structure/";
    private static final String DEFAULT_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String OTHER_ID = "550e8400-e29b-41d4-a716-446655440000";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetMyId_UnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get(GET_MY_ID_ENDPOINT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void testGetMyId_ReturnsIdForAuthenticatedUser() throws Exception {
        mockDefaultUser();

        mockMvc.perform(get(GET_MY_ID_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID));
    }

    @Test
    void testGetTreeStructure_ReturnsDefaultStructureForDefaultId() throws Exception {
        mockDefaultUser();

        mockMvc.perform(get(GET_STRUCTURE_ENDPOINT + DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$.father").exists())
                .andExpect(jsonPath("$.father.id").value("123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(jsonPath("$.father.firstName").value("John"))
                .andExpect(jsonPath("$.father.lastName").value("Smith"))
                .andExpect(jsonPath("$.father.familyName").value("Smith"))
                .andExpect(jsonPath("$.mother").exists())
                .andExpect(jsonPath("$.children").isArray())
                .andExpect(jsonPath("$.children.length()").value(3))
                .andExpect(jsonPath("$.children[0].firstName").value("Alice"))
                .andExpect(jsonPath("$.children[0].lastName").value("Smith"))
                .andExpect(jsonPath("$.children[0].familyName").value("Smith"));
    }

    @Test
    void testGetTreeStructure_RedirectsForOtherId() throws Exception {
        mockDefaultUser();

        mockMvc.perform(get(GET_STRUCTURE_ENDPOINT + OTHER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTreeStructure_InvalidId() throws Exception {
        mockDefaultUser();

        String invalidId = "invalid-uuid";

        mockMvc.perform(get(GET_STRUCTURE_ENDPOINT + invalidId))
                .andExpect(status().isBadRequest());
    }

    private void mockDefaultUser() {
        UserDto user = new UserDto();
        user.setLogin("testUser");
        user.setPassword("password");
        user.setDetailsId(UUID.fromString(DEFAULT_ID));

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "password", user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
