package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.mappers.UserMapper;
import com.aldhafara.genealogicalTree.models.UserModel;
import com.aldhafara.genealogicalTree.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.util.Locale.ROOT;

@Service
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
    public UserModel save(UserModel userModel) throws NotUniqueLogin {

        RegisterUser registerUser = userMapper.mapUserModelToRegisterUser(userModel);

        String lowerCaseLogin = registerUser.getLogin().toLowerCase(ROOT);

        if (userRepository.existsByLogin(lowerCaseLogin)) {
            throw new NotUniqueLogin();
        }

        registerUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        registerUser.setRoles("USER");
        registerUser.setLogin(lowerCaseLogin);
        RegisterUser savedUser = userRepository.save(registerUser);
        return userMapper.mapRegisterUserToUserModel(savedUser);
    }

    @Override
    public void update(UserModel userModel) {

        RegisterUser registerUser = userMapper.mapUserModelToRegisterUser(userModel);
        String lowerCaseLogin = registerUser.getLogin().toLowerCase(ROOT);
        registerUser.setRoles("USER");
        registerUser.setLogin(lowerCaseLogin);
        userRepository.save(registerUser);

    }
}
