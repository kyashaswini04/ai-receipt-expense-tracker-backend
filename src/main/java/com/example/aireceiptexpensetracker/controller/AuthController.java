package com.example.aireceiptexpensetracker.controller;

import com.example.aireceiptexpensetracker.entity.User;
import com.example.aireceiptexpensetracker.repository.UserRepository;
import com.example.aireceiptexpensetracker.config.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email already registered"));
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Registration successful",
                        "name", savedUser.getName(),
                        "email", savedUser.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        User existingUser = userRepository
                .findByEmail(user.getEmail())
                .orElse(null);

        if (existingUser == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid email or password"));
        }

        boolean passwordMatches = passwordEncoder.matches(
                user.getPassword(),
                existingUser.getPassword());

        if (!passwordMatches) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid email or password"));
        }

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful",
                        "name", existingUser.getName(),
                        "email", existingUser.getEmail(),
                        "token", jwtService.generateToken(existingUser.getEmail()),
                        "tokenType", "Bearer"));
    }
}
