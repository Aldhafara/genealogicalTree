package com.aldhafara.genealogicalTree.mappers;

import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.models.FamilyModel;
import org.springframework.stereotype.Component;

@Component
public class FamilyMapper {
    public Family mapFamilyModelToFamily(FamilyModel familyModel) {
        return Family.builder()
                .id(familyModel.getId())
                .addBy(familyModel.getAddBy())
                .father(familyModel.getFather())
                .mother(familyModel.getMother())
                .children(familyModel.getChildren())
                .updateDate(familyModel.getUpdateDate())
                .build();
    }

    public FamilyModel mapFamilyToFamilyModel(Family family) {
        return FamilyModel.builder()
                .id(family.getId())
                .addBy(family.getAddBy())
                .father(family.getFather())
                .mother(family.getMother())
                .children(family.getChildren())
                .updateDate(family.getUpdateDate())
                .build();
    }
}
