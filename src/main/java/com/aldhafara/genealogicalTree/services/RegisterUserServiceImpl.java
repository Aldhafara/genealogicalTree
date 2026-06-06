package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.mappers.UserMapper;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
import com.aldhafara.genealogicalTree.repositories.UserRepository;
import com.aldhafara.genealogicalTree.services.interfaces.RegisterUserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.util.Locale.ROOT;

@Service
@Slf4j
public class RegisterUserServiceImpl implements RegisterUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public RegisterUserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserDto save(UserDto userDto) throws NotUniqueLogin {

        RegisterUser registerUser = userMapper.mapUserDtoToRegisterUser(userDto);

        String lowerCaseLogin = registerUser.getLogin().toLowerCase(ROOT);

        if (userRepository.existsByLogin(lowerCaseLogin)) {
            log.warn("Save failed. login={} already exist",
                    lowerCaseLogin);
            throw new NotUniqueLogin();
        }

        registerUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        registerUser.setRoles("ROLE_USER");
        registerUser.setLogin(lowerCaseLogin);
        RegisterUser savedUser = userRepository.save(registerUser);
        return userMapper.mapRegisterUserToUserDto(savedUser);
    }

    @Override
    @Transactional
    public void updateDetailsId(UUID userId, UUID detailsId) {
        RegisterUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setDetailsId(detailsId);
    }
}
