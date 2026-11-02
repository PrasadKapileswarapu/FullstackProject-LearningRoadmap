package com.roadmap.service;

import java.util.Locale;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roadmap.dto.AuthResponse;
import com.roadmap.dto.LoginRequest;
import com.roadmap.dto.RegisterRequest;
import com.roadmap.exception.DuplicateResourceException;
import com.roadmap.model.AppUser;
import com.roadmap.repository.UserRepository;
import com.roadmap.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normaliseEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        AppUser user = new AppUser();
        user.setFullName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        AppUser saved = userRepository.save(user);

        log.info("Registered new user id={}", saved.getId());
        return toAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        String email = normaliseEmail(request.email());

        // Throws AuthenticationException (-> 401) when the email or password is wrong.
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        log.info("User id={} logged in", user.getId());
        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(AppUser user) {
        return new AuthResponse(
                jwtService.generateToken(user.getEmail()),
                "Bearer",
                jwtService.getExpirationMs(),
                user.getFullName(),
                user.getEmail());
    }

    private String normaliseEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
