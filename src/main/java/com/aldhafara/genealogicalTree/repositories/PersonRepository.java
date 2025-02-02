package com.aldhafara.genealogicalTree.repositories;

import com.aldhafara.genealogicalTree.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    List<Person> findByAddBy(UUID loggedUserId);

    @Query("SELECT p.id FROM Person p WHERE p.firstName = :firstName AND p.lastName = :lastName")
    Optional<UUID> findIdByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);
}
