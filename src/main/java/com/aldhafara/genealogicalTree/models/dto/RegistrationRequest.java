package com.aldhafara.genealogicalTree.models.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistrationRequest {
    private UserDto registerUser;
    private PersonDto personDetails;

    @JsonCreator
    public RegistrationRequest(@JsonProperty("registerUser") UserDto registerUser,
                               @JsonProperty("personDetails") PersonDto personDetails) {
        this.registerUser = registerUser;
        this.personDetails = personDetails;
    }

    public UserDto getRegisterUser() {
        return registerUser;
    }

    public void setRegisterUser(UserDto registerUser) {
        this.registerUser = registerUser;
    }

    public PersonDto getPersonDetails() {
        return personDetails;
    }

    public void setPersonDetails(PersonDto personDetails) {
        this.personDetails = personDetails;
    }
}
