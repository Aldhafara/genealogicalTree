package com.aldhafara.genealogicalTree.services.interfaces;

import com.aldhafara.genealogicalTree.exceptions.TreeStructureNotFoundException;
import com.aldhafara.genealogicalTree.models.dto.TreeStructuresDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface TreeDataService {

    TreeStructuresDto getTreeStructure(UUID id) throws TreeStructureNotFoundException;
}
