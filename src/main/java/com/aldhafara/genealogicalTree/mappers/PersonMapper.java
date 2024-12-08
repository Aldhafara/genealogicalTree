package com.aldhafara.genealogicalTree.mappers;

import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.models.PersonModel;
import com.aldhafara.genealogicalTree.models.UserModel;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    private final UserMapper userMapper;

    public PersonMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public PersonModel mapPersonToPersonModel(Person person) {
        UserModel userModel = userMapper.mapRegisterUserToUserModel(person.getAddBy());
        return PersonModel.builder()
                .id(person.getId())
                .addBy(userModel)
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .build();
    }

    public Person mapPersonModelToPerson(PersonModel personModel) {
        RegisterUser registerUser = userMapper.mapUserModelToRegisterUser(personModel.getAddBy());
        return Person.builder()
                .id(personModel.getId())
                .addBy(registerUser)
                .firstName(personModel.getFirstName())
                .lastName(personModel.getLastName())
                .build();
    }
}
