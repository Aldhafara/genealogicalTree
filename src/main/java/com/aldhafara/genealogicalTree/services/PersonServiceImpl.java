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
    private final Clock clock = Clock.systemUTC();

    public PersonServiceImpl(PersonRepository personRepository, PersonMapper personMapper, SecurityContextFacade securityContextFacade) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.securityContextFacade = securityContextFacade;
    }

    @Override
    public Person save(Person person) {
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
        person.setUpdateDate(clock.instant());

        if (person.getAddBy() == null) {
            UUID registerUserId = securityContextFacade.getCurrentUserId();
            person.setAddBy(registerUserId);
        }

        return saveAndReturnId(person);
    }

    @Transactional
    public Person saveAndReturn(PersonModel personModel) {
        Person person = personMapper.mapPersonModelToPerson(personModel);
        person.setUpdateDate(clock.instant());

        UUID registerUserId = securityContextFacade.getCurrentUserId();
        if (person.getAddBy() == null) {
            person.setAddBy(registerUserId);
        }

        return save(person);
    }

    public Person saveParent(PersonModel parent, Person child) {
        switch (parent.getSex()) {
            case MALE -> {
                switch (child.getSex()) {
                    case MALE -> parent.setLastName(child.getLastName());
                    case FEMALE -> parent.setLastName(child.getFamilyName().isBlank() ? child.getLastName() : child.getFamilyName());
                }
            }
            case FEMALE ->{
                switch (child.getSex()) {
                    case MALE -> parent.setLastName(child.getLastName());
                    case FEMALE -> parent.setLastName(child.getFamilyName().isBlank() ? child.getLastName() : child.getFamilyName());
                }
            }
        }
        return saveAndReturn(parent);
    }
}
