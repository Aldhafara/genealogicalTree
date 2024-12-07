package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin;
import com.aldhafara.genealogicalTree.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.util.Locale.ROOT;

@Service
public class RegisterUserServiceImpl implements RegisterUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(RegisterUser user) throws NotUniqueLogin {

        String lowerCaseLogin = user.getLogin().toLowerCase(ROOT);

        if (userRepository.existsByLogin(lowerCaseLogin)) {
            throw new NotUniqueLogin();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles("USER");
        user.setLogin(lowerCaseLogin);
        userRepository.save(user);
    }
}
