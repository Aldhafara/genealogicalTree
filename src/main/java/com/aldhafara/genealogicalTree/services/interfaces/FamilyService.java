package com.aldhafara.genealogicalTree.services.interfaces;

import com.aldhafara.genealogicalTree.entities.Family;

import java.util.List;
import java.util.UUID;

public interface FamilyService {

    Family save(Family family);
    List<Family> getFamiliesWithParent(UUID id);
    Family getFamilyByIdOrReturnNew(UUID familyId);
}
