package com.aldhafara.genealogicalTree.repositories;

import com.aldhafara.genealogicalTree.entities.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FamilyRepository extends JpaRepository<Family, UUID> {

    @Query("select f from Family f where f.mother.id = ?1 or f.father.id = ?1")
    List<Family> findByParent(UUID id);
}
