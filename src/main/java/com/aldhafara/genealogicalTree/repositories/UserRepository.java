package com.aldhafara.genealogicalTree.repositories;

import com.aldhafara.genealogicalTree.entities.RegisterUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<RegisterUser, UUID> {

    Optional<RegisterUser> findByLogin(String login);
}
