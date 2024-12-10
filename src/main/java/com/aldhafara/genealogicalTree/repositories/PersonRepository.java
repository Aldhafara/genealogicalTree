package com.aldhafara.genealogicalTree.repositories;

import com.aldhafara.genealogicalTree.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {}