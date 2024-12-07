package com.aldhafara.genealogicalTree.controllers;

import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }

    @GetMapping("/home")
    public String homePage(Model model,
                           @CurrentSecurityContext SecurityContext context) {
        model.addAttribute("userName", context.getAuthentication().getName());
        return "homePage";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

}
