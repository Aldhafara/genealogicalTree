package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserServiceImpl implements RegisterUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(RegisterUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles("USER");
        userRepository.save(user);
    }
}
