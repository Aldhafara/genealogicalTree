package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomerUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RegisterUser user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }
}
