package com.aldhafara.genealogicalTree.mappers;

import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto mapRegisterUserToUserDto(RegisterUser user) {
        return UserDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRoles())
                .detailsId(user.getDetailsId())
                .build();
    }

    public RegisterUser mapUserDtoToRegisterUser(UserDto user) {
        return RegisterUser.builder()
                .id(user.getId())
                .login(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRoles())
                .details(user.getDetailsId())
                .build();
    }
}
