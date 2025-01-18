package com.aldhafara.genealogicalTree.controllers.api;

import com.aldhafara.genealogicalTree.configuration.SecurityContextFacade;
import com.aldhafara.genealogicalTree.exceptions.TreeStructureNotFoundException;
import com.aldhafara.genealogicalTree.models.dto.FamilyTreeDto;
import com.aldhafara.genealogicalTree.services.interfaces.TreeDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/family-tree")
@Tag(name = "Tree Data Controller", description = "Operations related to drawing family tree structure")
public class TreeDataApiController {
    private final TreeDataService treeDataService;
    private final SecurityContextFacade securityContextFacade;

    public TreeDataApiController(TreeDataService treeDataService, SecurityContextFacade securityContextFacade) {
        this.treeDataService = treeDataService;
        this.securityContextFacade = securityContextFacade;
    }

    @Operation(
            summary = "Retrieve the current user's ID",
            description = "This endpoint returns the unique identifier of the currently authenticated user. If no user is authenticated, the behavior is undefined.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the user ID",
                            content = @Content(

                                    mediaType = "application/json",
                                    schema = @Schema(
                                            type = "string",
                                            example = "{ \"id\": \"123e4567-e89b-12d3-a456-426614174000\" }] }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized access (user not authenticated)"
                    )
            }
    )
    @GetMapping("/get-my-id")
    public ResponseEntity<?> getMyId() {
        UUID userId = securityContextFacade.getCurrentUserDetailsId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, String> response = Map.of("id", userId.toString());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Retrieve the family tree structure",
            description = "This endpoint returns the family tree structure for a specific user, identified by their unique ID. If the tree structure is not found, a 404 error will be returned.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the tree structure",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            type = "string",
                                            example = "{ \"id\": \"550e8400-e29b-41d4-a716-446655440000\", \"father\": { \"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"name\": \"John Smith\" }, \"mother\": { \"id\": \"987f6543-b21c-98e7-c567-456789123456\", \"name\": \"Jane Doe\" }, \"children\": [{ \"id\": \"d34db33f-0bad-cafe-1234-56789abcdef0\", \"name\": \"Alice Smith\" }, { \"id\": \"deadbeef-feed-babe-1234-567890abcdef\", \"name\": \"Bob Smith\" }] }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Tree structure not found for the given ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Tree structure not found")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request, invalid ID format",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Invalid UUID format")
                            )
                    )
            }
    )
    @GetMapping("/get-structure/{id}")
    public ResponseEntity<?> getTreeStructure(@PathVariable UUID id) {
        try {
            FamilyTreeDto treeStructure = treeDataService.getTreeStructure(id);
            return ResponseEntity.ok(treeStructure);
        } catch (TreeStructureNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Tree structure not found",
                            "message", ex.getMessage(),
                            "status", HttpStatus.NOT_FOUND.value()
                    ));
        }
    }
}
