package com.aldhafara.genealogicalTree.configuration;

import com.aldhafara.genealogicalTree.configuration.security.CustomAuthenticationFailureHandler;
import com.aldhafara.genealogicalTree.configuration.security.CustomAuthenticationSuccessHandler;
import com.aldhafara.genealogicalTree.repositories.UserRepository;
import com.aldhafara.genealogicalTree.services.CustomerUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private final UserRepository userRepository;
    private final CustomAuthenticationFailureHandler failureHandler;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CorrelationIdFilter correlationIdFilter;

    public SecurityConfiguration(UserRepository userRepository,
                                 CustomAuthenticationFailureHandler failureHandler, CustomAuthenticationSuccessHandler successHandler, CorrelationIdFilter correlationIdFilter) {
        this.userRepository = userRepository;
        this.failureHandler = failureHandler;
        this.successHandler = successHandler;
        this.correlationIdFilter = correlationIdFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomerUserDetailsService customUserDetailsService() {
        return new CustomerUserDetailsService(userRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(correlationIdFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/register", "/login").permitAll()
                        .requestMatchers("/js/**", "/locales/**", "/css/**", "/svg/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
