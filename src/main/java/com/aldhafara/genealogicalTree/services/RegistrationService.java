package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
import com.aldhafara.genealogicalTree.services.person.PersonServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RegistrationService {

    private final RegisterUserServiceImpl userService;
    private final PersonServiceImpl personService;

    public RegistrationService(RegisterUserServiceImpl userService, PersonServiceImpl personService) {
        this.userService = userService;
        this.personService = personService;
    }

    @Transactional
    public void register(UserDto userDto, PersonDto personDto) throws NotUniqueLogin {
        UserDto savedUser = userService.save(userDto);
        personDto.setAddBy(savedUser.getId());
        UUID savedPersonId = personService.saveAndReturnId(personDto, null);
        savedUser.setDetailsId(savedPersonId);
        userService.update(savedUser);
    }
}
