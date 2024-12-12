package com.aldhafara.genealogicalTree.mappers;

import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.models.PersonBasicData;
import com.aldhafara.genealogicalTree.models.PersonModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Component
public class PersonMapper {

    public PersonModel mapPersonToPersonModel(Person person, List<Family> familiesWithParent) {
        if (person == null) {
            return new PersonModel();
        }
        List<PersonBasicData> siblingsList = emptyList();
        List<PersonBasicData> children = new java.util.ArrayList<>(emptyList());
        PersonBasicData mother = null;
        PersonBasicData father = null;
        if (person.getFamily() != null) {
            List<Person> siblings = person.getFamily().getChildren();
            siblingsList = siblings.stream()
                    .filter(child -> !child.equals(person))
                    .map(PersonBasicData::new)
                    .collect(Collectors.toList());
            father = person.getFamily().getFather() != null ? new PersonBasicData(person.getFamily().getFather()) : null;
            mother = person.getFamily().getMother() != null ? new PersonBasicData(person.getFamily().getMother()) : null;
        }
        if (familiesWithParent != null && !familiesWithParent.isEmpty()) {
            familiesWithParent.forEach(family -> children.addAll(family.getChildren().stream().map(PersonBasicData::new).collect(Collectors.toList())));
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
                .siblings(siblingsList)
                .children(children)
                .father(father)
                .mother(mother)
                .build();
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
