package com.aldhafara.genealogicalTree.mappers;

import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.models.UserModel;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserModel mapRegisterUserToUserModel(RegisterUser user) {
        return UserModel.builder()
                .id(user.getId())
                .login(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRoles())
                .detailsId(user.getDetailsId())
                .build();
    }

    public RegisterUser mapUserModelToRegisterUser(UserModel user) {
        return RegisterUser.builder()
                .id(user.getId())
                .login(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRoles())
                .details(user.getDetailsId())
                .build();
    }
}
