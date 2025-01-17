package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.configuration.SecurityContextFacade;
import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.mappers.FamilyMapper;
import com.aldhafara.genealogicalTree.models.dto.FamilyDto;
import com.aldhafara.genealogicalTree.repositories.FamilyRepository;
import com.aldhafara.genealogicalTree.services.interfaces.FamilyService;
import jakarta.transaction.Transactional;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FamilyServiceImpl implements FamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMapper familyMapper;
    private final SecurityContextFacade securityContextFacade;
    @Setter
    private Clock clock = Clock.systemUTC();

    public FamilyServiceImpl(FamilyRepository familyRepository, FamilyMapper familyMapper, SecurityContextFacade securityContextFacade) {
        this.familyRepository = familyRepository;
        this.familyMapper = familyMapper;
        this.securityContextFacade = securityContextFacade;
    }

    @Override
    public Family save(Family family) {
        family.setUpdateDate(clock.instant());
        UUID registerUserId = securityContextFacade.getCurrentUserId();
        if (family.getAddBy() == null) {
            family.setAddBy(registerUserId);
        }
        return familyRepository.save(family);
    }

    public UUID saveAndReturnId(Family family) {
        return save(family).getId();
    }

    @Transactional
    public UUID save(FamilyDto familyDto) {
        Family family = familyMapper.mapFamilyDtoToFamily(familyDto);
        return saveAndReturnId(family);
    }

    public UUID saveChild(Family family, Person child) {
        if (child != null) {
            family.addChild(child);
        }
        return saveAndReturnId(family);
    }

    public UUID saveChild(Person firstParent, Person secondParent, Person child) {
        Family family = getFamilyByParentsOrReturnNew(firstParent, secondParent);
        return saveChild(family, child);
    }

    @Override
    public List<Family> getFamiliesWithParent(UUID parentId) {
        if (parentId == null) {
            return null;
        }
        return familyRepository.findByParent(parentId);
    }

    public Family getFamilyByIdOrReturnNew(UUID familyId) {
        if (familyId == null) {
            return null;
        }
        Optional<Family> familyOptional = familyRepository.findById(familyId);
        return familyOptional.orElse(null);
    }

    private Family getFamilyByParentsOrReturnNew(Person firstParent, Person secondParent) {
        if (firstParent == null || firstParent.getId() == null || secondParent == null || secondParent.getId() == null) {
            return null;
        }
        Optional<Family> familyOptional = familyRepository.findByParents(firstParent.getId(), secondParent.getId());
        return familyOptional.orElse(null);
    }

    public Family saveFamilyWithoutChildren(Person father, Person mother) {
        return saveFamilyWithChild(father, mother, null);
    }

    public Family saveFamilyWithChild(Person father, Person mother, Person child) {
        if (father == null || mother == null) {
            return null;
        }
        Family family = new Family(father, mother, child);
        return save(family);
    }
}
