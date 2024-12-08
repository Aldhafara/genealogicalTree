package com.aldhafara.genealogicalTree.controllers;

import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.models.PersonModel;
import com.aldhafara.genealogicalTree.models.UserModel;
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
    public String registerPage(Model model) {
        model.addAttribute("user", new UserModel());
        model.addAttribute("person", new PersonModel());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(Model model,
                               @ModelAttribute UserModel userModel,
                               @ModelAttribute PersonModel personModel) {
        try {
            UserModel saveUser = userService.save(userModel);
            personModel.setAddBy(saveUser);
            UUID savedPersonId = personService.save(personModel);
            saveUser.setDetailsId(savedPersonId);
            userService.update(saveUser);
            return "redirect:/login";
        } catch (NotUniqueLogin e) {
            model.addAttribute("loginError", String.format("Użytkownik %s już istnieje. Zmień login.", userModel.getLogin()));
            return "register";
        }
    }
}
