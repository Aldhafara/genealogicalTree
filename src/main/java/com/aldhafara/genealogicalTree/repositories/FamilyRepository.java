package com.aldhafara.genealogicalTree.repositories;

import com.aldhafara.genealogicalTree.entities.Family;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FamilyRepository extends JpaRepository<Family, UUID> {}
