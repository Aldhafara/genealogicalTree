package com.aldhafara.genealogicalTree.mappers;

import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.models.dto.FamilyDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class FamilyMapper {

    private final PersonMapper personMapper;

    public FamilyMapper(@Lazy PersonMapper personMapper) {
        this.personMapper = personMapper;
    }

    public Family mapFamilyDtoToFamily(FamilyDto familyDto) {
        return Family.builder()
                .id(familyDto.getId())
                .addBy(familyDto.getAddBy())
                .father(personMapper.mapPersonDtoToPerson(familyDto.getFather()))
                .mother(personMapper.mapPersonDtoToPerson(familyDto.getMother()))
                .children(familyDto.getChildren()
                        .stream()
                        .map(personMapper::mapPersonDtoToPerson)
                        .toList())
                .updateDate(familyDto.getUpdateDate())
                .build();
    }

    public FamilyDto mapFamilyToFamilyDto(Family family) {
        return FamilyDto.builder()
                .id(family.getId())
                .addBy(family.getAddBy())
                .father(personMapper.mapPersonToPersonDto(family.getFather(), null))
                .mother(personMapper.mapPersonToPersonDto(family.getMother(), null))
                .children(family.getChildren()
                        .stream()
                        .map(child -> personMapper.mapPersonToPersonDto(child, null))
                        .toList())
                .updateDate(family.getUpdateDate())
                .build();
    }
}
