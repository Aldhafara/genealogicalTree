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
import java.util.List;

import static java.util.Comparator.comparingDouble;

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
        persons.forEach(personDto -> personDto.setMatchResult(personMatcher.similarityScore(personDto, loggedPerson)));
        List<PersonDto> topMatchedPersons = persons.stream()
                .filter(p -> p.getMatchResult().comparedFields() >= 3)
                .sorted(comparingDouble((PersonDto p) -> p.getMatchResult().score()).reversed())
                .toList();
        if (topMatchedPersons.isEmpty()) {
            throw new PersonNotFoundException();
        }

        if (topMatchedPersons.get(0).getMatchResult().score() >= 0.9) {
            personService.saveAll(persons);
        }
        // TODO [Refactor idea]:
        // Instead of saving all persons from the input list directly to the database,
        // introduce a new dedicated service that will:
        //
        // (a) If there are NO family members in the DB for the currently logged-in user:
        //     - Save all persons from the list `persons` into the DB.
        // (b) If there ARE existing family members for this user:
        //     - Compare each new person with existing DB persons (using similarity scoring).
        //     - Show the user a comparison view with two records:
        //         1. Person from DB
        //         2. Person we want to add
        //       - Let the user confirm if it's the same person or not.
        //     - Based on user decision: update existing record or insert new person.
        //
        // This will prevent duplicate records and allow the user to control merges.
    }
}
