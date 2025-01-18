package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.exceptions.TreeStructureNotFoundException;
import com.aldhafara.genealogicalTree.mappers.PersonMapper;
import com.aldhafara.genealogicalTree.models.dto.FamilyTreeDto;
import com.aldhafara.genealogicalTree.repositories.FamilyRepository;
import com.aldhafara.genealogicalTree.services.interfaces.TreeDataService;
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
    public FamilyTreeDto getTreeStructure(UUID id) throws TreeStructureNotFoundException {

        List<Family> families = familyRepository.findByParent(id);
        if (families == null || families.isEmpty()) {
            throw new TreeStructureNotFoundException("Tree structure for person with ID " + id + " not found");
        }

        FamilyTreeDto familyTreeRoot = new FamilyTreeDto();
        families.forEach(family -> {
            familyTreeRoot.setId(family.getId());
            familyTreeRoot.setFather(personMapper.mapPersonToPersonDto(family.getFather(), null));
            familyTreeRoot.setMother(personMapper.mapPersonToPersonDto(family.getMother(), null));
            Optional.ofNullable(family.getChildren())
                    .ifPresent(children -> children.forEach(child ->
                            familyTreeRoot.getChildren().add(personMapper.mapPersonToPersonDto(child, null))
                    ));
            familyTreeRoot.setMarriageDate(family.getUpdateDate()); //TODO Zmienić na marriageDate
        });
        familyTreeRoot.getChildren().forEach(System.out::println);

        return familyTreeRoot;
    }
}
