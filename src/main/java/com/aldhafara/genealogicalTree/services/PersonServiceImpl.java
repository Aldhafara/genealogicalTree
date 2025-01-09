package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.configuration.SecurityContextFacade;
import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException;
import com.aldhafara.genealogicalTree.mappers.PersonMapper;
import com.aldhafara.genealogicalTree.models.PersonBasicData;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import com.aldhafara.genealogicalTree.repositories.PersonRepository;
import com.aldhafara.genealogicalTree.services.interfaces.PersonService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
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

    @Transactional
    @Override
    public Person save(Person person) {
        if (person == null) {
            person = new Person();
        }
        person.setUpdateDate(clock.instant());
        return personRepository.save(person);
    }

    @Override
    public Person getById(UUID id) throws PersonNotFoundException {

        Optional<Person> optionalPerson = personRepository.findById(id);
        if (optionalPerson.isEmpty()) {
            throw new PersonNotFoundException();
        }
        return optionalPerson.get();
    }

    public Person saveAndReturn(PersonDto personDto) {
        Person person = setAddByLoggedUserId(personMapper.mapPersonDtoToPerson(personDto));

        return save(person);
    }

    private Person setAddByLoggedUserId(Person person) {
        if (person != null && person.getAddBy() == null) {
            UUID registerUserId = securityContextFacade.getCurrentUserId();
            person.setAddBy(registerUserId);
        }
        return person;
    }

    public Person saveParent(PersonDto parent, Person person) {
        return saveAndReturn(determinateParentLastName(parent, person));
    }

    private PersonDto determinateParentLastName(PersonDto parent, Person person) {
        switch (person.getSex()) {
            case MALE -> parent.setLastName(person.getLastName());
            case FEMALE -> parent.setLastName(person.getFamilyName() == null || person.getFamilyName().isBlank() ? person.getLastName() : person.getFamilyName());
        }
        return parent;
    }

    public Person saveSibling(PersonDto sibling, Person person) {
        return saveAndReturn(determinateSiblingLastName(sibling, person));
    }

    private PersonDto determinateSiblingLastName(PersonDto sibling, Person person) {
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
        return sibling;
    }

    public UUID saveAndReturnId(PersonDto personDto, Family family) {
        Person savedPerson = saveAndReturnPerson(personDto, family);
        return savedPerson.getId();
    }

    public Person saveAndReturnPerson(PersonDto personDto, Family family) {
        Person person = setAddByLoggedUserId(personMapper.mapPersonDtoWithFamilyToPerson(personDto, family));
        if (person == null) {
            return null;
        }

        return save(person);
    }

    public List<PersonBasicData> getAll() {
        return personRepository
                .findByAddBy(securityContextFacade.getCurrentUserId())
                .stream()
                .map(PersonBasicData::new)
                .toList();
    }

    public Person createChildAndSave(PersonDto child, Person firstParent, Person secondParent) {
        switch (firstParent.getSex()) {
            case MALE -> {
                return saveAndReturn(determinateChildLastName(child, firstParent, secondParent));
            }
            case FEMALE -> {
                return saveAndReturn(determinateChildLastName(child, secondParent, firstParent));
            }
        }
        return saveAndReturn(determinateChildLastName(child, firstParent, secondParent));
    }

    private PersonDto determinateChildLastName(PersonDto child, Person father, Person mother) {
        String fatherLastName = father.getLastName();
        String motherLastName = mother.getLastName();
        String motherFamilyName = mother.getFamilyName();
        if (motherFamilyName != null && !motherFamilyName.isBlank()) {
            child.setLastName(motherFamilyName);
        }
        if (motherLastName != null && !motherLastName.isBlank()) {
            child.setLastName(motherLastName);
        }
        if (fatherLastName != null && !fatherLastName.isBlank()) {
            child.setLastName(fatherLastName);
        }
        return child;
    }
}
