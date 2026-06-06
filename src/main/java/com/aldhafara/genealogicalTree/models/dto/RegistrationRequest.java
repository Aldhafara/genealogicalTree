package com.aldhafara.genealogicalTree.models.dto;

import com.aldhafara.genealogicalTree.models.dto.registration.RegisterUserRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationRequest {

    @NotNull
    @Valid
    private RegisterUserRequest registerUser;

    @NotNull
    @Valid
    private PersonDto personDetails;
}
