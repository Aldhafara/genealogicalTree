package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.models.UserModel;
import com.aldhafara.genealogicalTree.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static java.util.Locale.ROOT;

public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomerUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RegisterUser user = userRepository.findByLogin(username.toLowerCase(ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return UserModel.builder()
                .id(user.getId())
                .login(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRoles())
                .detailsId(user.getDetailsId())
                .build();
    }
}
