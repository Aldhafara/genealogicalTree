package com.aldhafara.genealogicalTree.controllers;

import com.aldhafara.genealogicalTree.exceptions.TreeStructureNotFoundException;
import com.aldhafara.genealogicalTree.models.dto.TreeStructuresDto;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
import com.aldhafara.genealogicalTree.services.interfaces.TreeDataService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
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

    @Mock
    private TreeDataService treeDataService;

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
    void testGetTreeStructure_Returns404WhenServiceThrowException() throws Exception {
        mockDefaultUser();

        when(treeDataService.getTreeStructure(UUID.fromString(DEFAULT_ID)))
                .thenThrow(new TreeStructureNotFoundException(""));

        mockMvc.perform(get(GET_STRUCTURE_ENDPOINT + DEFAULT_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    @Disabled
    void testGetTreeStructure_ReturnsCorrectStructureForDefaultId() throws Exception {
        mockDefaultUser();

        UUID uuid = UUID.fromString(DEFAULT_ID);
        System.out.println(uuid);
        when(treeDataService.getTreeStructure(uuid))
                .thenReturn(createFamilyTreeDto());
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

    private TreeStructuresDto createFamilyTreeDto() {
        TreeStructuresDto treeStructuresDto = new TreeStructuresDto();
        TreeStructuresDto.TreeStructure family = new TreeStructuresDto.TreeStructure();
        family.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        family.setFather(new PersonDto.Builder()
                .id(UUID.fromString(DEFAULT_ID))
                .firstName("John")
                .lastName("Smith")
                .familyName("Smith")
                .build());
        family.setMother(new PersonDto.Builder()
                .firstName("Jane")
                .lastName("Smith")
                .build());

        PersonDto child1 = new PersonDto.Builder()
                .firstName("Alice")
                .lastName("Smith")
                .familyName("Smith")
                .build();
        PersonDto child2 = new PersonDto.Builder()
                .firstName("Bob")
                .lastName("Smith")
                .familyName("Smith")
                .build();
        PersonDto child3 = new PersonDto.Builder()
                .firstName("Charlie")
                .lastName("Smith")
                .familyName("Smith")
                .build();
        family.setChildren(List.of(child1, child2, child3));

        treeStructuresDto.getFamilies().add(family);
        return treeStructuresDto;
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
