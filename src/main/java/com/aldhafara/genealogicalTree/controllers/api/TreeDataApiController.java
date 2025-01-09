package com.aldhafara.genealogicalTree.controllers.api;

import com.aldhafara.genealogicalTree.exceptions.TreeStructureNotFoundException;
import com.aldhafara.genealogicalTree.services.dev.TreeDataServiceDummyImpl;
import com.aldhafara.genealogicalTree.services.interfaces.TreeDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/family-tree")
@Tag(name = "Tree Data Controller", description = "Operations related to drawing family tree structure")
public class TreeDataApiController {

    private final TreeDataService treeDataService;

    public TreeDataApiController(TreeDataService treeDataService) {
        this.treeDataService = new TreeDataServiceDummyImpl();
    }

    @GetMapping("/get-my-id")
    public ResponseEntity<String> getMyId(@CurrentSecurityContext SecurityContext context) {
        return ResponseEntity.status(HttpStatus.OK)
                .body("123e4567-e89b-12d3-a456-426614174000");
//        UserDto userDto = (UserDto) context.getAuthentication().getPrincipal();
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(userDto.getDetailsId().toString());
    }
    @GetMapping("/get-structure/{id}")
    public ResponseEntity<String> getTreeStructure(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(treeDataService.getTreeStructure(id));
        } catch (TreeStructureNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }
}
