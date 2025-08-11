package com.arij.fashionecommerce.service;

import com.arij.fashionecommerce.entity.AuthProvider;
import com.arij.fashionecommerce.entity.Role;
import com.arij.fashionecommerce.entity.User;
import com.arij.fashionecommerce.repository.RoleRepository;
import com.arij.fashionecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//This logic safely handles three cases: user previously signed in with Google, same email existed as LOCAL (we link), or brand new Google-only account (we create).
@Service
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    public UserService(UserRepository ur, RoleRepository rr) {
        this.userRepo = ur;
        this.roleRepo = rr;
    }

    @Transactional
    public User processOAuthPostLogin(String email, String name, String providerId, AuthProvider provider) {
        // 1. if providerId (google sub) exists -> return user
        return userRepo.findByProviderId(providerId).orElseGet(() -> {
            // 2. If local user with same email exists -> link accounts (or raise a conflict)
            return userRepo.findByEmail(email).map(existing -> {
                existing.setProvider(provider);
                existing.setProviderId(providerId);
                // don't overwrite password
                return userRepo.save(existing);
            }).orElseGet(() -> {
                // 3. else create new user
                User user = new User();
                user.setEmail(email);
                user.setFullName(name);
                user.setProvider(provider);
                user.setProviderId(providerId);
                Role userRole = roleRepo.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("ROLE_USER not set"));
                user.getRoles().add(userRole);
                return userRepo.save(user);
            });
        });
    }
}