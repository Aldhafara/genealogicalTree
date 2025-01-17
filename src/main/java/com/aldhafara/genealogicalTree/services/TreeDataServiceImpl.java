package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException;
import com.aldhafara.genealogicalTree.exceptions.TreeStructureNotFoundException;
import com.aldhafara.genealogicalTree.mappers.PersonMapper;
import com.aldhafara.genealogicalTree.models.dto.FamilyTreeDto;
import com.aldhafara.genealogicalTree.repositories.FamilyRepository;
import com.aldhafara.genealogicalTree.repositories.PersonRepository;
import com.aldhafara.genealogicalTree.services.interfaces.TreeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TreeDataServiceImpl implements TreeDataService {

    private final PersonRepository personRepository;
    private final FamilyRepository familyRepository;
    private final PersonMapper personMapper;

    @Autowired
    public TreeDataServiceImpl(PersonRepository personRepository, FamilyRepository familyRepository, PersonMapper personMapper) {
        this.personRepository = personRepository;
        this.familyRepository = familyRepository;
        this.personMapper = personMapper;
    }

    @Override
    public FamilyTreeDto getTreeStructure(UUID id) {
        try {
            personRepository.findById(id)
                    .orElseThrow(PersonNotFoundException::new);
        } catch (PersonNotFoundException e) {
            throw new TreeStructureNotFoundException("Tree structure for person with ID " + id + " not found");
        }

        FamilyTreeDto familyTreeRoot = new FamilyTreeDto();

        List<Family> families = familyRepository.findByParent(id);

        families.forEach(family -> {
            familyTreeRoot.setId(family.getId());
            familyTreeRoot.setFather(personMapper.mapPersonToPersonDto( family.getFather(), null));
            familyTreeRoot.setMother(personMapper.mapPersonToPersonDto( family.getMother(), null));
            family.getChildren().forEach(child -> familyTreeRoot.getChildren().add(personMapper.mapPersonToPersonDto(child, null)));

            familyTreeRoot.setMarriageDate(family.getUpdateDate()); //TODO Zmienić na marriageDate
        });

        return familyTreeRoot;
    }
}
