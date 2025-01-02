package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface RegisterUserService {

    UserDto save(UserDto user) throws NotUniqueLogin;

    void update(UserDto userDto) throws NotUniqueLogin;
}
