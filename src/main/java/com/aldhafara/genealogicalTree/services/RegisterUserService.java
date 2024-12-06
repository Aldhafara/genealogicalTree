package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.RegisterUser;
import org.springframework.stereotype.Service;

@Service
public interface RegisterUserService {

    void registerUser(RegisterUser user);
}
