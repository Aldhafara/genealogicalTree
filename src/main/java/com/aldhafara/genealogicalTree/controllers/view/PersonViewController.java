package com.aldhafara.genealogicalTree.controllers.view;

import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException;
import com.aldhafara.genealogicalTree.mappers.PersonMapper;
import com.aldhafara.genealogicalTree.models.PersonBasicData;
import com.aldhafara.genealogicalTree.models.SexEnum;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import com.aldhafara.genealogicalTree.services.FamilyServiceImpl;
import com.aldhafara.genealogicalTree.services.person.PersonServiceImpl;
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
@RequestMapping("/person")
public class PersonViewController {

    private final PersonServiceImpl personService;
    private final FamilyServiceImpl familyService;
    private final PersonMapper personMapper;

    public PersonViewController(PersonServiceImpl personService, FamilyServiceImpl familyService, PersonMapper personMapper) {
        this.personService = personService;
        this.familyService = familyService;
        this.personMapper = personMapper;
    }

    @GetMapping("/{id}")
    public String getDetails(@PathVariable UUID id, Model model) {
        try {
            PersonDto personDto = personMapper.mapPersonToPersonDto(
                    personService.getById(id), familyService.getFamiliesWithParent(id));
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
        } catch (PersonNotFoundException e) {
            model.addAttribute("errorMessage", "Person not found");
            return "error";
        }
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
}
