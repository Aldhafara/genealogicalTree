package com.aldhafara.genealogicalTree.controllers;

import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException;
import com.aldhafara.genealogicalTree.mappers.PersonMapper;
import com.aldhafara.genealogicalTree.models.FamilyModel;
import com.aldhafara.genealogicalTree.models.PersonModel;
import com.aldhafara.genealogicalTree.models.SexEnum;
import com.aldhafara.genealogicalTree.models.UserModel;
import com.aldhafara.genealogicalTree.services.FamilyServiceImpl;
import com.aldhafara.genealogicalTree.services.PersonServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping(value = "/person")
public class PersonController {
    private final PersonServiceImpl personService;
    private final FamilyServiceImpl familyService;
    private final PersonMapper personMapper;

    public PersonController(PersonServiceImpl personService, FamilyServiceImpl familyService, PersonMapper personMapper) {
        this.personService = personService;
        this.familyService = familyService;
        this.personMapper = personMapper;
    }

    @GetMapping("/aboutme")
    public String aboutMe(@CurrentSecurityContext SecurityContext context) {
        UserModel userModel = (UserModel) context.getAuthentication().getPrincipal();
        return "redirect:/person/" + userModel.getDetailsId();
    }

    @GetMapping("/{id}")
    public Object getDetails(@PathVariable UUID id,
                             Model model) {
        PersonModel personModel;
        try {
            personModel = personMapper.mapPersonToPersonModel(personService.getById(id), familyService.getFamiliesWithParent(id));
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>(
                    "Person not found", HttpStatus.NOT_FOUND);
        }
        model.addAttribute("person", personModel);
        model.addAttribute("siblings", personModel.getSiblings());
        model.addAttribute("children", personModel.getChildren());
        model.addAttribute("mother", personModel.getMother());
        model.addAttribute("father", personModel.getFather());
        model.addAttribute("sexOptions", SexEnum.values());
        return "personDetails";
    }

    @PostMapping("/edit")
    public String editPerson(@ModelAttribute PersonModel personModel) {
        personService.saveAndReturnId(personModel, familyService.getFamilyById(personModel.getFamilyId()));

        return "redirect:/person/" + personModel.getId();
    }

    @GetMapping("/add")
    public String addPerson() {
        PersonModel personModel = new PersonModel();
        UUID newPersonId = personService.saveAndReturnId(personModel, familyService.getFamilyById(personModel.getFamilyId()));

        return "redirect:/person/" + newPersonId;
    }

    @GetMapping("/add/for/{id}/mother")
    public Object addMother(@PathVariable UUID id) {
        Person child;
        try {
            child = personService.getById(id);
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>(
                    "Person not found", HttpStatus.NOT_FOUND);
        }
        PersonModel parentModel = new PersonModel();
        parentModel.setSex(SexEnum.FEMALE);
        Person parent = personService.saveParent(parentModel, child);

        familyService.save(getFamilyModel(child, parent));

        return "redirect:/person/" + parent.getId();
    }

    @GetMapping("/add/for/{id}/father")
    public Object addFather(@PathVariable UUID id) {
        Person child;
        try {
            child = personService.getById(id);
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>(
                    "Person not found", HttpStatus.NOT_FOUND);
        }
        PersonModel parentModel = new PersonModel();
        parentModel.setSex(SexEnum.MALE);
        Person parent = personService.saveParent(parentModel, child);

        familyService.save(getFamilyModel(child, parent));

        return "redirect:/person/" + parent.getId();
    }

    @GetMapping("/add/for/{id}/sister")
    public Object addSister(@PathVariable UUID id) {
        Person savedSibling;
        try {
            savedSibling = addSibling(id, SexEnum.FEMALE);
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>(
                    "Person not found", HttpStatus.NOT_FOUND);
        }

        return "redirect:/person/" + savedSibling.getId();
    }

    @GetMapping("/add/for/{id}/brother")
    public Object addBrother(@PathVariable UUID id) {
        Person savedSibling;
        try {
            savedSibling = addSibling(id, SexEnum.MALE);
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>(
                    "Person not found", HttpStatus.NOT_FOUND);
        }
        return "redirect:/person/" + savedSibling.getId();
    }

    private Person addSibling(UUID id, SexEnum female) throws PersonNotFoundException {
        Person person = personService.getById(id);

        PersonModel sibling = new PersonModel();
        sibling.setSex(female);
        Person savedSibling = personService.saveSibling(sibling, person);
        familyService.save(person.getFamily(), savedSibling);
        return savedSibling;
    }

    private FamilyModel getFamilyModel(Person child, Person parent) {
        FamilyModel familyModel;
        if (child.getFamily() != null) {
            familyModel = familyService.getFamilyModel(child.getFamily());
        } else {
            familyModel = new FamilyModel();
        }

        if (!familyModel.hasChild(child.getId())) {
            familyModel.addChild(child);
        }
        switch (parent.getSex()) {
            case FEMALE -> familyModel.setMother(parent);
            case MALE -> familyModel.setFather(parent);
        }
        return familyModel;
    }
}
