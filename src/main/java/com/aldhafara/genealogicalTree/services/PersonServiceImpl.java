package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.configuration.SecurityContextFacade;
import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException;
import com.aldhafara.genealogicalTree.mappers.PersonMapper;
import com.aldhafara.genealogicalTree.models.PersonModel;
import com.aldhafara.genealogicalTree.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final SecurityContextFacade securityContextFacade;

    public PersonServiceImpl(PersonRepository personRepository, PersonMapper personMapper, SecurityContextFacade securityContextFacade) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.securityContextFacade = securityContextFacade;
    }

    @Override
    public UUID save(Person person) {
        Person savedPerson = personRepository.save(person);
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
    public UUID save(PersonModel personModel) {
        Person person = personMapper.mapPersonModelToPerson(personModel);
        person.setUpdateDate(Instant.now());

        UUID registerUserId = securityContextFacade.getCurrentUserId();
        if (person.getAddBy() == null) {
            person.setAddBy(registerUserId);
        }

        Person savedPerson = personRepository.save(person);
        return savedPerson.getId();
    }
}
