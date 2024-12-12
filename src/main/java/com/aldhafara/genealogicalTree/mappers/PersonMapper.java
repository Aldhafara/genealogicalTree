package com.aldhafara.genealogicalTree.mappers;

import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.models.PersonBasicData;
import com.aldhafara.genealogicalTree.models.PersonModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PersonMapper {

    public PersonModel mapPersonToPersonModel(Person person, List<Family> familiesWithPersonAsParent) {
        if (person == null) {
            return new PersonModel();
        }

        List<PersonBasicData> siblingsWithStepSiblingsList = new ArrayList<>();
        List<PersonBasicData> children = new ArrayList<>();
        List<PersonBasicData> partners = new ArrayList<>();
        PersonBasicData mother = null;
        PersonBasicData father = null;

        if (person.getFamily() != null) {
            List<Person> siblings = person.getFamily().getChildren();
            siblingsWithStepSiblingsList = Optional.ofNullable(siblings)
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(child -> !child.equals(person))
                    .map(PersonBasicData::new)
                    .collect(Collectors.toList());

            father = person.getFamily().getFather() != null ? new PersonBasicData(person.getFamily().getFather()) : null;
            mother = person.getFamily().getMother() != null ? new PersonBasicData(person.getFamily().getMother()) : null;
        }

        if (familiesWithPersonAsParent != null && !familiesWithPersonAsParent.isEmpty()) {
            familiesWithPersonAsParent.stream()
                    .flatMap(family -> Optional.ofNullable(family.getChildren())
                            .orElse(Collections.emptyList())
                            .stream())
                    .map(PersonBasicData::new)
                    .forEach(children::add);

            familiesWithPersonAsParent.forEach(family -> {
                addPartnerIfNotPerson(partners, family.getFather(), person);
                addPartnerIfNotPerson(partners, family.getMother(), person);
            });
        }

        return PersonModel.builder()
                .id(person.getId())
                .addBy(person.getAddBy())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .updateDate(person.getUpdateDate())
                .familyName(person.getFamilyName())
                .sex(person.getSex())
                .setBirthDateFromInstant(person.getBirthDate())
                .birthPlace(person.getBirthPlace())
                .siblingsWithStepSiblings(siblingsWithStepSiblingsList)
                .children(children)
                .father(father)
                .mother(mother)
                .partners(partners)
                .build();
    }

    private void addPartnerIfNotPerson(List<PersonBasicData> partners, Person partner, Person person) {
        Optional.ofNullable(partner)
                .filter(p -> p != person)
                .ifPresent(p -> partners.add(new PersonBasicData(p)));
    }

    public Person mapPersonModelToPerson(PersonModel personModel) {
        return Person.builder()
                .id(personModel.getId())
                .addBy(personModel.getAddBy())
                .firstName(personModel.getFirstName())
                .lastName(personModel.getLastName())
                .updateDate(personModel.getUpdateDate())
                .familyName(personModel.getFamilyName())
                .sex(personModel.getSex())
                .birthDate(personModel.getBirthDateAsInstant())
                .birthPlace(personModel.getBirthPlace())
                .build();
    }

    public Person mapPersonModelWithFamilyToPerson(PersonModel personModel, Family family) {
        return Person.builder()
                .id(personModel.getId())
                .addBy(personModel.getAddBy())
                .firstName(personModel.getFirstName())
                .lastName(personModel.getLastName())
                .updateDate(personModel.getUpdateDate())
                .familyName(personModel.getFamilyName())
                .sex(personModel.getSex())
                .birthDate(personModel.getBirthDateAsInstant())
                .birthPlace(personModel.getBirthPlace())
                .family(family)
                .build();
    }
}
