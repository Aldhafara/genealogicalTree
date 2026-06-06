package com.aldhafara.genealogicalTree.controllers.view;

import com.aldhafara.genealogicalTree.controllers.api.RegistrationApiController;
import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.models.SexEnum;
import com.aldhafara.genealogicalTree.models.dto.RegistrationRequest;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
import com.aldhafara.genealogicalTree.services.CustomerUserDetailsService;
import com.aldhafara.genealogicalTree.services.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationViewController {
    private final CustomerUserDetailsService userDetailsService;
    private final RegistrationApiController registrationApiController;
    private final RegistrationService registrationService;

    public RegistrationViewController(CustomerUserDetailsService userDetailsService, RegistrationApiController registrationApiController, RegistrationService registrationService) {
        this.userDetailsService = userDetailsService;
        this.registrationApiController = registrationApiController;
        this.registrationService = registrationService;
    }

    @GetMapping
    public String getRegisterPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null && !userDetailsService.userHasRole(userDetails, "ADMIN")) {
            return "redirect:/home";
        }

        model.addAttribute("registrationRequest", new RegistrationRequest());
        model.addAttribute("sexOptions", SexEnum.values());
        return "register";
    }

    @PostMapping
    public String registerUser(Model model,
                               @Valid @ModelAttribute("registrationRequest") RegistrationRequest request,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("sexOptions", SexEnum.values());
            return "register";
        }

        try {
            UserDto userDto = UserDto.builder()
                    .login(request.getRegisterUser().getLogin())
                    .password(request.getRegisterUser().getPassword())
                    .build();

            registrationService.register(userDto, request.getPersonDetails());
            return "redirect:/login";
        } catch (NotUniqueLogin e) {
            model.addAttribute("sexOptions", SexEnum.values());
            bindingResult.rejectValue("registerUser.login", "login.exists", "User login must be unique");
            return "register";
        }
    }
}
