package com.aldhafara.genealogicalTree.controllers;

import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException;
import com.aldhafara.genealogicalTree.mappers.PersonMapper;
import com.aldhafara.genealogicalTree.models.PersonBasicData;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import com.aldhafara.genealogicalTree.models.SexEnum;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
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
        UserDto userDto = (UserDto) context.getAuthentication().getPrincipal();
        return "redirect:/person/" + userDto.getDetailsId();
    }

    @GetMapping("/{id}")
    public Object getDetails(@PathVariable UUID id,
                             Model model) {
        PersonDto personDto;
        try {
            personDto = personMapper.mapPersonToPersonDto(personService.getById(id), familyService.getFamiliesWithParent(id));
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>(
                    "Person not found", HttpStatus.NOT_FOUND);
        }
        model.addAttribute("person", personDto);
        model.addAttribute("siblingsWithStepSiblings", personDto.getSiblingsWithStepSiblings());
        model.addAttribute("siblings", personDto.getSiblings());
        model.addAttribute("children", personDto.getChildren());
        model.addAttribute("mother", personDto.getMother());
        model.addAttribute("father", personDto.getFather());
        model.addAttribute("partners", personDto.getPartners());
        model.addAttribute("families", personDto.getFamiliesAsParent());
        model.addAttribute("sexOptions", SexEnum.values());
        return "personDetails";
    }

    @PostMapping("/edit")
    public String editPerson(@ModelAttribute PersonDto personDto) {
        personService.saveAndReturnId(personDto, familyService.getFamilyByIdOrReturnNew(personDto.getFamilyId()));

        return "redirect:/person/" + personDto.getId();
    }

    @GetMapping("/add")
    public String addPerson() {
        PersonDto personDto = new PersonDto();
        UUID newPersonId = personService.saveAndReturnId(personDto, familyService.getFamilyByIdOrReturnNew(personDto.getFamilyId()));
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
            savedPerson = personService.save(null);
            if (person.getSex() == SexEnum.MALE) {
                familyService.saveFamilyWithoutChildren(person, savedPerson);
            } else {
                familyService.saveFamilyWithoutChildren(savedPerson, person);
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
            familyService.saveFamilyWithChild(father, mother, person);
        }

        return parentSex == SexEnum.MALE ? father.getId() : mother.getId();
    }

    private Person createAndSaveParent(SexEnum parentSex, Person child) {
        PersonDto parentModel = new PersonDto();
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

        PersonDto sibling = new PersonDto();
        sibling.setSex(siblingSex);

        Person savedSibling = personService.saveSibling(sibling, person);
        familyService.saveChild(person.getFamily(), savedSibling);

        return savedSibling;
    }

    @GetMapping("/add/child/{childType}/for/{firstParentId}/{secondParentId}")
    public Object addChild(@PathVariable UUID firstParentId, @PathVariable UUID secondParentId, @PathVariable String childType) {
        SexEnum childSex;
        try {
            childSex = determineChildSex(childType);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid child type", HttpStatus.BAD_REQUEST);
        }
        Person firstPerson;
        Person secondPerson;
        try {
            firstPerson = personService.getById(firstParentId);
            secondPerson = personService.getById(secondParentId);
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>("Person not found", HttpStatus.NOT_FOUND);
        }
        Person savedChild;
        try {
            savedChild = createChild(firstPerson, secondPerson, childSex);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid parents", HttpStatus.BAD_REQUEST);
        }
        return "redirect:/person/" + savedChild.getId();
    }

    private SexEnum determineChildSex(String childType) {
        return switch (childType.toLowerCase()) {
            case "daughter" -> SexEnum.FEMALE;
            case "son" -> SexEnum.MALE;
            default -> throw new IllegalArgumentException("Invalid sibling type");
        };
    }

    private Person createChild(Person firstParent, Person secondParent, SexEnum childSex) {
        if (firstParent == null || secondParent == null) {
            throw new IllegalArgumentException("A child cannot exist without parents");
        }

        PersonDto child = new PersonDto();
        child.setSex(childSex);

        Person savedChild = personService.createChildAndSave(child, firstParent, secondParent);
        familyService.saveChild(firstParent, secondParent, savedChild);

        return savedChild;
    }
}
