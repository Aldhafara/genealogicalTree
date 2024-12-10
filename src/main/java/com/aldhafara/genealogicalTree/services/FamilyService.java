package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.Family;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface FamilyService {
    UUID save(Family family);
}
