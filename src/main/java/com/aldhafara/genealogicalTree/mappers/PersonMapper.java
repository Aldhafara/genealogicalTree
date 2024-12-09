package com.aldhafara.genealogicalTree.mappers;

import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.models.PersonModel;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    public PersonModel mapPersonToPersonModel(Person person) {
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
}
