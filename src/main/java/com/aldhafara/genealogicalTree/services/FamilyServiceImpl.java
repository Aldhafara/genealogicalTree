package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.configuration.SecurityContextFacade;
import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.mappers.FamilyMapper;
import com.aldhafara.genealogicalTree.models.FamilyModel;
import com.aldhafara.genealogicalTree.repositories.FamilyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.UUID;

@Service
public class FamilyServiceImpl implements FamilyService{

    private final FamilyRepository familyRepository;
    private final FamilyMapper familyMapper;
    private final SecurityContextFacade securityContextFacade;
    private final Clock clock = Clock.systemUTC();

    public FamilyServiceImpl(FamilyRepository familyRepository, FamilyMapper familyMapper, SecurityContextFacade securityContextFacade) {
        this.familyRepository = familyRepository;
        this.familyMapper = familyMapper;
        this.securityContextFacade = securityContextFacade;
    }

    @Override
    public UUID save(Family family) {
        Family savedFamily = familyRepository.save(family);
        return savedFamily.getId();
    }


    @Transactional
    public UUID save(FamilyModel familyModel) {
        Family family = familyMapper.mapFamilyModelToFamily(familyModel);
        family.setUpdateDate(clock.instant());

        UUID registerUserId = securityContextFacade.getCurrentUserId();
        if (family.getAddBy() == null) {
            family.setAddBy(registerUserId);
        }

        return save(family);
    }

    public FamilyModel getFamilyModel(Family family) {
        return familyMapper.mapFamilyToFamilyModel(family);
    }
}
