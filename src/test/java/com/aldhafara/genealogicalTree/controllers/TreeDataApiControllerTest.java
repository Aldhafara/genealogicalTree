package com.aldhafara.genealogicalTree.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    @WithMockUser(username = "testUser")
    void testGetMyId_ReturnsIdForAuthenticatedUser() throws Exception {
        mockMvc.perform(get(GET_MY_ID_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string(DEFAULT_ID));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testGetTreeStructure_ReturnsDefaultStructureForDefaultId() throws Exception {
        mockMvc.perform(get(GET_STRUCTURE_ENDPOINT + DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"father\": {")));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testGetTreeStructure_RedirectsForOtherId() throws Exception {
        mockMvc.perform(get(GET_STRUCTURE_ENDPOINT + OTHER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser")
    void testGetTreeStructure_InvalidId() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(get(GET_STRUCTURE_ENDPOINT + invalidId))
                .andExpect(status().isBadRequest());
    }
}
