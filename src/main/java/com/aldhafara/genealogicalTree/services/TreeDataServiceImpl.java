package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.exceptions.TreeStructureNotFoundException;
import com.aldhafara.genealogicalTree.services.interfaces.TreeDataService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Profile("default")
public class TreeDataServiceImpl implements TreeDataService {

    @Override
    public String getTreeStructure(UUID id) {
        throw new TreeStructureNotFoundException(id.toString());
    }

}
