package com.aldhafara.genealogicalTree.models.dto.registration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterUserRequest {

    @NotBlank(message = "{validation.login.required}")
    @Size(min = 3, max = 255)
    @Pattern(
            regexp = "^[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*$",
            message = "{validation.login.invalidFormat}"
    )
    private String login;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 8, max = 255)
    @Pattern(
            regexp = "^[A-Za-z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~]+$",
            message = "{validation.password.unsupportedCharacters}"
    )
    private String password;
}
