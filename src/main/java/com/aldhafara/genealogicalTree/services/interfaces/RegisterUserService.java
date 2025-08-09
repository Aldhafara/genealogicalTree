package com.aldhafara.genealogicalTree.services.interfaces;

import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.models.dto.UserDto;

public interface RegisterUserService {

    UserDto save(UserDto user) throws NotUniqueLogin;

    void update(UserDto userDto) throws NotUniqueLogin;
}
