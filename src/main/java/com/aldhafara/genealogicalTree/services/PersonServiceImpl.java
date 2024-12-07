package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.repositories.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UUID save(Person person) {
        Person savedPerson = personRepository.save(person);
        return savedPerson.getId();
    }

    public UUID save(Person person, RegisterUser user) {
        person.setAddBy(user);
        Person savedPerson = personRepository.save(person);
        return savedPerson.getId();
    }
}
