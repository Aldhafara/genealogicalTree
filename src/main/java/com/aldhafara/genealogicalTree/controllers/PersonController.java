package com.aldhafara.genealogicalTree.controllers;

import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException;
import com.aldhafara.genealogicalTree.mappers.PersonMapper;
import com.aldhafara.genealogicalTree.models.PersonBasicData;
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

import java.util.List;
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
        model.addAttribute("siblingsWithStepSiblings", personModel.getSiblingsWithStepSiblings());
        model.addAttribute("children", personModel.getChildren());
        model.addAttribute("mother", personModel.getMother());
        model.addAttribute("father", personModel.getFather());
        model.addAttribute("partners", personModel.getPartners());
        model.addAttribute("sexOptions", SexEnum.values());
        return "personDetails";
    }

    @PostMapping("/edit")
    public String editPerson(@ModelAttribute PersonModel personModel) {
        personService.saveAndReturnId(personModel, familyService.getFamilyByIdOrReturnNew(personModel.getFamilyId()));

        return "redirect:/person/" + personModel.getId();
    }

    @GetMapping("/add")
    public String addPerson() {
        PersonModel personModel = new PersonModel();
        UUID newPersonId = personService.saveAndReturnId(personModel, familyService.getFamilyByIdOrReturnNew(personModel.getFamilyId()));
        return "redirect:/person/" + newPersonId;
    }


    @GetMapping("/all")
    public String getAllPersons(Model model) {
        List<PersonBasicData> allPersons = personService.getAll();
        model.addAttribute("allPersons", allPersons);
        return "allPersons";
    }

    @GetMapping("/add/parent/{parentType}/for/{personId}")
    public Object addParent(@PathVariable UUID personId, @PathVariable String parentType) {
        SexEnum parentSex;
        try {
            parentSex = determineParentSex(parentType);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid parent type", HttpStatus.BAD_REQUEST);
        }

        UUID parentId;
        try {
            Person person = personService.getById(personId);
            parentId = getParentUuid(person, parentSex);
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>("Person not found", HttpStatus.NOT_FOUND);
        }

        return "redirect:/person/" + parentId;
    }

    @GetMapping("/add/partner/for/{personId}")
    public Object addPartner(@PathVariable UUID personId) {

        Person savedPerson;
        try {
            Person person = personService.getById(personId);
            Person partner = new Person();
            savedPerson = personService.save(partner);
            if (person.getSex() == SexEnum.MALE) {
                familyService.save(new Family(person, savedPerson, null));
            } else {
                familyService.save(new Family(savedPerson, person, null));
            }
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>("Person not found", HttpStatus.NOT_FOUND);
        }

        return "redirect:/person/" + savedPerson.getId();
    }

    private SexEnum determineParentSex(String parentType) {
        return switch (parentType.toLowerCase()) {
            case "mother" -> SexEnum.FEMALE;
            case "father" -> SexEnum.MALE;
            default -> throw new IllegalArgumentException("Invalid parent type");
        };
    }

    private UUID getParentUuid(Person person, SexEnum parentSex) {
        Person mother = createAndSaveParent(SexEnum.FEMALE, person);
        Person father = createAndSaveParent(SexEnum.MALE, person);

        if (person.getFamily() == null || person.getFamily().getId() == null) {
            familyService.save(new Family(father, mother, person));
        }

        return parentSex == SexEnum.MALE ? father.getId() : mother.getId();
    }

    private Person createAndSaveParent(SexEnum parentSex, Person child) {
        PersonModel parentModel = new PersonModel();
        parentModel.setSex(parentSex);
        return personService.saveParent(parentModel, child);
    }

    @GetMapping("/add/sibling/{siblingType}/for/{personId}")
    public Object addSibling(@PathVariable UUID personId, @PathVariable String siblingType) {
        SexEnum siblingSex;
        try {
            siblingSex = determineSiblingSex(siblingType);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid sibling type", HttpStatus.BAD_REQUEST);
        }

        Person savedSibling;
        try {
            savedSibling = createSibling(personId, siblingSex);
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>("Person not found", HttpStatus.NOT_FOUND);
        }

        return "redirect:/person/" + savedSibling.getId();
    }

    private SexEnum determineSiblingSex(String siblingType) {
        return switch (siblingType.toLowerCase()) {
            case "sister" -> SexEnum.FEMALE;
            case "brother" -> SexEnum.MALE;
            default -> throw new IllegalArgumentException("Invalid sibling type");
        };
    }

    private Person createSibling(UUID personId, SexEnum siblingSex) throws PersonNotFoundException {
        Person person = personService.getById(personId);

        PersonModel sibling = new PersonModel();
        sibling.setSex(siblingSex);

        Person savedSibling = personService.saveSibling(sibling, person);
        familyService.saveChild(person.getFamily(), savedSibling);

        return savedSibling;
    }
}
