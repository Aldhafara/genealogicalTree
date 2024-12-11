package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.configuration.SecurityContextFacade;
import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException;
import com.aldhafara.genealogicalTree.mappers.PersonMapper;
import com.aldhafara.genealogicalTree.models.PersonModel;
import com.aldhafara.genealogicalTree.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final SecurityContextFacade securityContextFacade;
    private Clock clock = Clock.systemUTC();

    public PersonServiceImpl(PersonRepository personRepository, PersonMapper personMapper, SecurityContextFacade securityContextFacade) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.securityContextFacade = securityContextFacade;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Person save(Person person) {
        person.setUpdateDate(clock.instant());
        return personRepository.save(person);
    }

    public UUID saveAndReturnId(Person person) {
        Person savedPerson = save(person);
        return savedPerson.getId();
    }

    @Override
    public Person getById(UUID id) throws PersonNotFoundException {

        Optional<Person> optionalPerson = personRepository.findById(id);
        if (optionalPerson.isEmpty()) {
            throw new PersonNotFoundException();
        }
        return optionalPerson.get();
    }

    @Transactional
    public UUID saveAndReturnId(PersonModel personModel) {
        Person person = personMapper.mapPersonModelToPerson(personModel);

        if (person.getAddBy() == null) {
            UUID registerUserId = securityContextFacade.getCurrentUserId();
            person.setAddBy(registerUserId);
        }

        return saveAndReturnId(person);
    }

    @Transactional
    public Person saveAndReturn(PersonModel personModel) {
        Person person = personMapper.mapPersonModelToPerson(personModel);

        UUID registerUserId = securityContextFacade.getCurrentUserId();
        if (person.getAddBy() == null) {
            person.setAddBy(registerUserId);
        }

        return save(person);
    }

    public Person saveParent(PersonModel parent, Person person) {
        switch (person.getSex()) {
            case MALE -> parent.setLastName(person.getLastName());
            case FEMALE -> parent.setLastName(person.getFamilyName() == null || person.getFamilyName().isBlank() ? person.getLastName() : person.getFamilyName());
        }
        return saveAndReturn(parent);
    }

    public Person saveSibling(PersonModel sibling, Person person) {
        switch (sibling.getSex()) {
            case MALE -> {
                switch (person.getSex()) {
                    case MALE -> sibling.setLastName(person.getLastName());
                    case FEMALE -> sibling.setLastName(person.getFamilyName().isBlank() ? person.getLastName() : person.getFamilyName());
                }
            }
            case FEMALE ->{
                switch (person.getSex()) {
                    case MALE -> sibling.setFamilyName(person.getLastName());
                    case FEMALE -> sibling.setFamilyName(person.getFamilyName().isBlank() ? person.getLastName() : person.getFamilyName());
                }
            }
        }
        return saveAndReturn(sibling);
    }
}
