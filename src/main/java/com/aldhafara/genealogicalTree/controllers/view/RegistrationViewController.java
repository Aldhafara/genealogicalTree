package com.aldhafara.genealogicalTree.controllers.view;

import com.aldhafara.genealogicalTree.controllers.api.RegistrationApiController;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import com.aldhafara.genealogicalTree.models.dto.RegistrationRequest;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
import com.aldhafara.genealogicalTree.services.CustomerUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/register")
public class RegistrationViewController {
    private final CustomerUserDetailsService userDetailsService;
    private final RegistrationApiController registrationApiController;

    public RegistrationViewController(CustomerUserDetailsService userDetailsService, RegistrationApiController registrationApiController) {
        this.userDetailsService = userDetailsService;
        this.registrationApiController = registrationApiController;
    }

    @GetMapping
    public String getRegisterPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null && !userDetailsService.userHasRole(userDetails, "ADMIN")) {
            return "redirect:/home";
        }
        Map<String, Object> apiData = registrationApiController.getRegistrationData();
        model.addAttribute("user", new UserDto());
        model.addAttribute("person", new PersonDto());
        model.addAttribute("sexOptions", apiData.get("sexOptions"));
        return "register";
    }

    @PostMapping
    public String registerUser(Model model,
                               @ModelAttribute UserDto userDto,
                               @ModelAttribute PersonDto personDto) {
        ResponseEntity<String> responseEntity = registrationApiController.registerUser(new RegistrationRequest(userDto, personDto));
        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            return "redirect:/login";
        } else {
            return "register";
        }
    }
}
