package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.exceptions.TreeStructureNotFoundException;
import com.aldhafara.genealogicalTree.mappers.PersonMapper;
import com.aldhafara.genealogicalTree.models.SexEnum;
import com.aldhafara.genealogicalTree.models.dto.TreeStructuresDto;
import com.aldhafara.genealogicalTree.repositories.FamilyRepository;
import com.aldhafara.genealogicalTree.services.interfaces.TreeDataService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TreeDataServiceImpl implements TreeDataService {

    private final FamilyRepository familyRepository;
    private final PersonMapper personMapper;

    public TreeDataServiceImpl(FamilyRepository familyRepository, PersonMapper personMapper) {
        this.familyRepository = familyRepository;
        this.personMapper = personMapper;
    }

    @Override
    @Cacheable(value = "treeStructures", key = "#id")
    public TreeStructuresDto getTreeStructure(UUID id) throws TreeStructureNotFoundException {

        List<Family> families = familyRepository.findByParent(id);
        if (families == null || families.isEmpty()) {
            throw new TreeStructureNotFoundException("Tree structure for person with ID " + id + " not found");
        }

        TreeStructuresDto familyTreeRoot = new TreeStructuresDto();
        families.forEach(family -> {
            TreeStructuresDto.TreeStructure familyTree = new TreeStructuresDto.TreeStructure();

            familyTree.setId(family.getId());
            familyTree.setFather(personMapper.mapPersonToPersonDto(family.getFather(), null));
            familyTree.setMother(personMapper.mapPersonToPersonDto(family.getMother(), null));
            Optional.ofNullable(family.getChildren())
                    .ifPresent(children -> children.forEach(child ->
                            familyTree.getChildren().add(personMapper.mapPersonToPersonDto(child, null))
                    ));
            familyTree.setMarriageDate(family.getUpdateDate()); //TODO Zmienić na marriageDate

            familyTreeRoot.getFamilies().add(familyTree);
        });
        if (families.get(0).getMother() != null) {
            familyTreeRoot.setMainPersonSex(families.get(0).getMother().getId() == id ? SexEnum.FEMALE : SexEnum.MALE);
        }
        return familyTreeRoot;
    }
}
