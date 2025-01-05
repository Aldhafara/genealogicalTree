package com.aldhafara.genealogicalTree.controllers.api;

import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.models.SexEnum;
import com.aldhafara.genealogicalTree.models.dto.RegistrationRequest;
import com.aldhafara.genealogicalTree.services.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/register")
@Tag(name = "Registration API", description = "API operations for user registration")
public class RegistrationApiController {

    private final RegistrationService registrationService;

    public RegistrationApiController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    @Operation(
            summary = "Get the registration page",
            description = "Displays the registration form for creating a new user and person. Includes options for selecting sex.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Registration form displayed successfully."
                    )
            }
    )
    public Map<String, Object> getRegistrationData() {
        Map<String, Object> data = new HashMap<>();
        data.put("sexOptions", SexEnum.values());
        return data;
    }

    @PostMapping
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user and associated person in the system.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully."),
                    @ApiResponse(responseCode = "400", description = "Registration failed due to non-unique login.")
            }
    )
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest request) {
        try {
            registrationService.register(request.getRegisterUser(), request.getPersonDetails());
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (NotUniqueLogin e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User login must be unique");
        }
    }
}