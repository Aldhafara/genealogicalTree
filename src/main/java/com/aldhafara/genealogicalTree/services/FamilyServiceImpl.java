package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.configuration.SecurityContextFacade;
import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.mappers.FamilyMapper;
import com.aldhafara.genealogicalTree.models.FamilyModel;
import com.aldhafara.genealogicalTree.repositories.FamilyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FamilyServiceImpl implements FamilyService{

    private final FamilyRepository familyRepository;
    private final FamilyMapper familyMapper;
    private final SecurityContextFacade securityContextFacade;
    private Clock clock = Clock.systemUTC();

    public FamilyServiceImpl(FamilyRepository familyRepository, FamilyMapper familyMapper, SecurityContextFacade securityContextFacade) {
        this.familyRepository = familyRepository;
        this.familyMapper = familyMapper;
        this.securityContextFacade = securityContextFacade;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
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
    public UUID save(FamilyModel familyModel) {
        Family family = familyMapper.mapFamilyModelToFamily(familyModel);
        return saveAndReturnId(family);
    }

    public UUID saveChild(Family family, Person child) {
        if (child != null) {
            family.addChild(child);
        }
        return saveAndReturnId(family);
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
}
