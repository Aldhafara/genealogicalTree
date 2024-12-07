package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.Person;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface PersonService {
    UUID save(Person person);
}
