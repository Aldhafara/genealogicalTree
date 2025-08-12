package com.aldhafara.genealogicalTree.services.gedcom;

import com.aldhafara.genealogicalTree.configuration.SecurityContextFacade;
import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException;
import com.aldhafara.genealogicalTree.mappers.PersonDtoMapper;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import com.aldhafara.genealogicalTree.services.person.PersonMatcher;
import com.aldhafara.genealogicalTree.services.person.PersonServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class GedcomImportService {
    private final PersonServiceImpl personService;
    private final PersonDtoMapper personDtoMapper;
    private final SecurityContextFacade securityContextFacade;
    private final PersonMatcher personMatcher;

    public GedcomImportService(PersonServiceImpl personService, PersonDtoMapper personDtoMapper, SecurityContextFacade securityContextFacade, PersonMatcher personMatcher) {
        this.personService = personService;
        this.personDtoMapper = personDtoMapper;
        this.securityContextFacade = securityContextFacade;
        this.personMatcher = personMatcher;
    }

    public void importFromGedcom(List<String> gedcomPeople) throws PersonNotFoundException {
        List<PersonDto> persons = new ArrayList<>();

        for (String gedcomPerson : gedcomPeople) {
            var person = personDtoMapper.mapJsonPersonToPerson(gedcomPerson);
            persons.add(person);
        }
        if (persons.isEmpty()) {
            throw new PersonNotFoundException();
        }

        Person loggedPerson = personService.getById(securityContextFacade.getCurrentUserDetailsId());
        persons.forEach(personDto -> personDto.setSimilarity(personMatcher.similarityScore(personDto, loggedPerson)));
        persons.sort(Comparator.comparingDouble(PersonDto::getSimilarity).reversed());

        //TODO Add new service
        //TODO Add view to compare actual logged person details to the most similar person from persons list

        if (persons.get(0).getSimilarity() >= 0.9) {
            persons.forEach(personService::saveAndReturn);
        }
    }
}
