package com.aldhafara.genealogicalTree.controllers;

import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.services.RegisterUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final RegisterUserService userService;

    public RegistrationController(RegisterUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegisterUser());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterUser user) {
        userService.registerUser(user);
        return "redirect:/login";
    }
}
