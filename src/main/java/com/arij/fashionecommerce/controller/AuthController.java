package com.arij.fashionecommerce.controller;

import com.arij.fashionecommerce.dto.JwtAuthenticationResponse;
import com.arij.fashionecommerce.dto.LoginRequest;
import com.arij.fashionecommerce.dto.RegisterRequest;
import com.arij.fashionecommerce.entity.AuthProvider;
import com.arij.fashionecommerce.entity.Role;
import com.arij.fashionecommerce.entity.User;
import com.arij.fashionecommerce.repository.RoleRepository;
import com.arij.fashionecommerce.repository.UserRepository;
import com.arij.fashionecommerce.security.JWT.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, JwtTokenProvider tokenProvider,
                          UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder) {
        this.authenticationManager = authManager;
        this.tokenProvider = tokenProvider;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already in use"));
        }
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFullName(req.getFullName());
        user.setProvider(AuthProvider.LOCAL);

        Role userRole = roleRepo.findByName("ROLE_USER").orElseThrow();
        user.getRoles().add(userRole);
        userRepo.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        String token = tokenProvider.generateToken(authentication);
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(Map.of(
                "token", token,
                "roles", user.getRoles().stream().map(Role::getName).toList(),
                "email", user.getEmail(),
                "fullName", user.getFullName()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow();

        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "roles", user.getRoles().stream().map(Role::getName).toList()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

}
