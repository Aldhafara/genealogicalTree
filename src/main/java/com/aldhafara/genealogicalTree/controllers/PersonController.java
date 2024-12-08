package com.aldhafara.genealogicalTree.controllers;

import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException;
import com.aldhafara.genealogicalTree.mappers.PersonMapper;
import com.aldhafara.genealogicalTree.models.PersonModel;
import com.aldhafara.genealogicalTree.models.UserModel;
import com.aldhafara.genealogicalTree.services.PersonServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping(value = "/person")
public class PersonController {
    private final PersonServiceImpl personService;
    private final PersonMapper personMapper;

    public PersonController(PersonServiceImpl personService, PersonMapper personMapper) {
        this.personService = personService;
        this.personMapper = personMapper;
    }

    @GetMapping("/aboutme")
    public String aboutMe(@CurrentSecurityContext SecurityContext context) {
        UserModel userModel = (UserModel) context.getAuthentication().getPrincipal();
        return "redirect:/person/" + userModel.getDetailsId();
    }

    @GetMapping("/{id}")
    public Object getDetails(@CurrentSecurityContext SecurityContext context,
                             @PathVariable UUID id,
                             Model model) {
        PersonModel personModel = null;
        try {
            personModel = personMapper.mapPersonToPersonModel(personService.getById(id));
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>(
                    "Person not found", HttpStatus.NOT_FOUND);
        }
        model.addAttribute("person", personModel);
        return "personDetails";
    }
}
