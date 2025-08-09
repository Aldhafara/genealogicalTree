package com.aldhafara.genealogicalTree.services.interfaces;

import com.aldhafara.genealogicalTree.exceptions.TreeStructureNotFoundException;
import com.aldhafara.genealogicalTree.models.dto.TreeStructuresDto;

import java.util.UUID;

public interface TreeDataService {

    TreeStructuresDto getTreeStructure(UUID id) throws TreeStructureNotFoundException;
}
