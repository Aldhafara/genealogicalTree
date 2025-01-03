package com.aldhafara.genealogicalTree.controllers;

import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import com.aldhafara.genealogicalTree.models.SexEnum;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
import com.aldhafara.genealogicalTree.services.PersonServiceImpl;
import com.aldhafara.genealogicalTree.services.RegisterUserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Registration Controller", description = "Operations related to registration")
public class RegistrationController {

    private final RegisterUserServiceImpl userService;
    private final PersonServiceImpl personService;

    public RegistrationController(RegisterUserServiceImpl userService, PersonServiceImpl personService) {
        this.userService = userService;
        this.personService = personService;
    }

    @GetMapping("/register")
    @Operation(
            summary = "Get the registration page",
            description = "Displays the registration form for creating a new user and person. Includes options for selecting sex.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Registration form displayed successfully.",
                            content = @Content(mediaType = "text/html")
                    )
            }
    )
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("person", new PersonDto());
        model.addAttribute("sexOptions", SexEnum.values());
        return "register";
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Processes the registration form, creating a new user and an associated person in the system. Redirects to the login page upon success or re-displays the form with an error message if the login is not unique.",
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "User registered successfully. Redirect to the login page.",
                            headers = @Header(name = "Location",
                                    description = "URL of the login page",
                                    schema = @Schema(type = "string", example = "/login"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Registration failed due to non-unique login."
                    )
            }
    )
    public String registerUser(Model model,
                               @ModelAttribute UserDto userDto,
                               @ModelAttribute PersonDto personDto) {
        try {
            UserDto saveUser = userService.save(userDto);
            personDto.setAddBy(saveUser.getId());
            UUID savedPersonId = personService.saveAndReturnId(personDto, null);
            saveUser.setDetailsId(savedPersonId);
            userService.update(saveUser);
            return "redirect:/login";
        } catch (NotUniqueLogin e) {
            model.addAttribute("loginError", String.format("Użytkownik %s już istnieje. Zmień login.", userDto.getLogin()));
            return "register";
        }
    }
}
