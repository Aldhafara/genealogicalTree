package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import org.springframework.stereotype.Service;

@Service
public interface RegisterUserService {

    void registerUser(RegisterUser user) throws NotUniqueLogin;
}
