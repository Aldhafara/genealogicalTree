package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.models.UserModel;
import org.springframework.stereotype.Service;

@Service
public interface RegisterUserService {

    UserModel save(UserModel user) throws NotUniqueLogin;

    void update(UserModel userModel) throws NotUniqueLogin;
}
