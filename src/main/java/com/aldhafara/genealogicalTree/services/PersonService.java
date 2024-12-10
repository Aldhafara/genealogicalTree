package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface PersonService {
    Person save(Person person);
    Person getById(UUID id) throws PersonNotFoundException;
}
