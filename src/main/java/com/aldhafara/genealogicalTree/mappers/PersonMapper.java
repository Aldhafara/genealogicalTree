package com.aldhafara.genealogicalTree.mappers;

import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.models.dto.FamilyDto;
import com.aldhafara.genealogicalTree.models.PersonBasicData;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PersonMapper {

    private final FamilyMapper familyMapper;

    public PersonMapper(@Lazy FamilyMapper familyMapper) {
        this.familyMapper = familyMapper;
    }

    public PersonDto mapPersonToPersonDto(Person person, List<Family> familiesWithPersonAsParent) {
        if (person == null) {
            return new PersonDto();
        }

        List<PersonBasicData> siblingsList = new ArrayList<>();
        List<PersonBasicData> children = new ArrayList<>();
        List<PersonBasicData> partners = new ArrayList<>();
        List<FamilyDto> familiesAsParent = new ArrayList<>();
        PersonBasicData mother = null;
        PersonBasicData father = null;

        if (person.getFamily() != null) {
            List<Person> siblings = person.getFamily().getChildren();
            siblingsList = Optional.ofNullable(siblings)
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
                familiesAsParent.add(familyMapper.mapFamilyToFamilyDto(family));
                addPartnerIfNotSamePerson(partners, family.getFather(), person);
                addPartnerIfNotSamePerson(partners, family.getMother(), person);
            });
        }

        return PersonDto.builder()
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
                .partners(partners)
                .familiesAsParent(familiesAsParent)
                .build();
    }

    private void addPartnerIfNotSamePerson(List<PersonBasicData> partners, Person partner, Person person) {
        Optional.ofNullable(partner)
                .filter(p -> p != person)
                .ifPresent(p -> partners.add(new PersonBasicData(p)));
    }

    public Person mapPersonDtoToPerson(PersonDto personDto) {
        return Person.builder()
                .id(personDto.getId())
                .addBy(personDto.getAddBy())
                .firstName(personDto.getFirstName())
                .lastName(personDto.getLastName())
                .updateDate(personDto.getUpdateDate())
                .familyName(personDto.getFamilyName())
                .sex(personDto.getSex())
                .birthDate(personDto.getBirthDateAsInstant())
                .birthPlace(personDto.getBirthPlace())
                .build();
    }

    public Person mapPersonDtoWithFamilyToPerson(PersonDto personDto, Family family) {
        return Person.builder()
                .id(personDto.getId())
                .addBy(personDto.getAddBy())
                .firstName(personDto.getFirstName())
                .lastName(personDto.getLastName())
                .updateDate(personDto.getUpdateDate())
                .familyName(personDto.getFamilyName())
                .sex(personDto.getSex())
                .birthDate(personDto.getBirthDateAsInstant())
                .birthPlace(personDto.getBirthPlace())
                .family(family)
                .build();
    }
}
