package com.example.aireceiptexpensetracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // Disable CSRF because we are using REST APIs
                .csrf(csrf -> csrf.disable())

                // Allow frontend requests from localhost
                .cors(cors -> {
                })

                // API access rules
                .authorizeHttpRequests(auth -> auth

                        // Authentication APIs must be public
                        .requestMatchers("/auth/**").permitAll()

                        // Application APIs
                        .requestMatchers(
                                "/expenses/**",
                                "/receipts/**",
                                "/dashboard/**",
                                "/money/**",
                                "/loans/**",
                                "/payments/**",
                                "/budget/**")
                        .permitAll()

                        // Everything else requires authentication
                        .anyRequest().authenticated());

        return http.build();
    }

    /*
     * Password Encoder
     *
     * Passwords will NOT be stored as plain text.
     * BCrypt will convert the user's password
     * into a secure hashed password before saving it.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}