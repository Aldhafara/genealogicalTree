package com.aldhafara.genealogicalTree.controllers;

import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.services.PersonServiceImpl;
import com.aldhafara.genealogicalTree.services.RegisterUserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
        model.addAttribute("user", new RegisterUser());
        model.addAttribute("person", new Person());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(Model model,
                               @ModelAttribute RegisterUser user,
                               @ModelAttribute Person person) {
        try {
            userService.registerUser(user);
            personService.save(person, user);
            return "redirect:/login";
        } catch (NotUniqueLogin e) {
            model.addAttribute("loginError", String.format("Użytkownik %s już istnieje. Zmień login.", user.getLogin()));
            return "register";
        }
    }
}
