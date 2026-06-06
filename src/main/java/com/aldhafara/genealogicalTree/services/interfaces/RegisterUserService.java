package com.aldhafara.genealogicalTree.services.interfaces;

import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.models.dto.UserDto;

import java.util.UUID;

public interface RegisterUserService {

    UserDto save(UserDto user) throws NotUniqueLogin;

    void updateDetailsId(UUID userId, UUID detailsId);
}
