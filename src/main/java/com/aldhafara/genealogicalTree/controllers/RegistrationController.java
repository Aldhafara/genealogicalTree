package com.aldhafara.genealogicalTree.controllers;

import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import com.aldhafara.genealogicalTree.models.SexEnum;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
import com.aldhafara.genealogicalTree.services.PersonServiceImpl;
import com.aldhafara.genealogicalTree.services.RegisterUserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
public class RegistrationController {

    private final RegisterUserServiceImpl userService;
    private final PersonServiceImpl personService;

    public RegistrationController(RegisterUserServiceImpl userService, PersonServiceImpl personService) {
        this.userService = userService;
        this.personService = personService;
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("person", new PersonDto());
        model.addAttribute("sexOptions", SexEnum.values());
        return "register";
    }

    @PostMapping("/register")
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
