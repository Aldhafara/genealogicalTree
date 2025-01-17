package com.aldhafara.genealogicalTree.services.interfaces;

import com.aldhafara.genealogicalTree.exceptions.TreeStructureNotFoundException;
import com.aldhafara.genealogicalTree.models.dto.FamilyTreeDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface TreeDataService {

    FamilyTreeDto getTreeStructure(UUID id) throws TreeStructureNotFoundException;
}
