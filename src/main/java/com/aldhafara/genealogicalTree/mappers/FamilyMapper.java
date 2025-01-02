package com.aldhafara.genealogicalTree.mappers;

import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.models.dto.FamilyDto;
import org.springframework.stereotype.Component;

@Component
public class FamilyMapper {
    public Family mapFamilyDtoToFamily(FamilyDto familyDto) {
        return Family.builder()
                .id(familyDto.getId())
                .addBy(familyDto.getAddBy())
                .father(familyDto.getFather())
                .mother(familyDto.getMother())
                .children(familyDto.getChildren())
                .updateDate(familyDto.getUpdateDate())
                .build();
    }

    public FamilyDto mapFamilyToFamilyDto(Family family) {
        return FamilyDto.builder()
                .id(family.getId())
                .addBy(family.getAddBy())
                .father(family.getFather())
                .mother(family.getMother())
                .children(family.getChildren())
                .updateDate(family.getUpdateDate())
                .build();
    }
}
