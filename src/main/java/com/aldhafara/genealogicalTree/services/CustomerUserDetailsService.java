package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.RegisterUser;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
import com.aldhafara.genealogicalTree.repositories.UserRepository;
import org.apache.commons.lang3.StringUtils;
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

        return UserDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRoles())
                .detailsId(user.getDetailsId())
                .build();
    }

    public boolean userHasRole(UserDetails userDetails, String role) {
        if (StringUtils.isBlank(role) || userDetails == null) {
            return false;
        }
        UserDto user = (UserDto) userDetails;
        return user.hasRole(role.toUpperCase());
    }
}
