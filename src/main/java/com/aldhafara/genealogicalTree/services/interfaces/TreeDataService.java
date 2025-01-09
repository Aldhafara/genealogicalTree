package com.aldhafara.genealogicalTree.services.interfaces;

import com.aldhafara.genealogicalTree.exceptions.TreeStructureNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface TreeDataService {

    String getTreeStructure(UUID id) throws TreeStructureNotFoundException;
}
