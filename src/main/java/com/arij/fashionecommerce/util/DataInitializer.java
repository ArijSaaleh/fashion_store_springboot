package com.arij.fashionecommerce.util;

import com.arij.fashionecommerce.entity.AuthProvider;
import com.arij.fashionecommerce.entity.Role;
import com.arij.fashionecommerce.entity.User;
import com.arij.fashionecommerce.repository.RoleRepository;
import com.arij.fashionecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public DataInitializer(RoleRepository rr, UserRepository ur, PasswordEncoder pe) {
        this.roleRepo = rr; this.userRepo = ur; this.encoder = pe;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepo.findByName("ROLE_USER").isEmpty()) roleRepo.save(new Role(null, "ROLE_USER"));
        if (roleRepo.findByName("ROLE_ADMIN").isEmpty()) roleRepo.save(new Role(null, "ROLE_ADMIN"));

        // create admin
        String adminEmail = "admin@example.com";
        if (userRepo.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(encoder.encode("AdminPass123!"));
            admin.setFullName("Super Admin");
            admin.setProvider(AuthProvider.LOCAL);
            Role adminRole = roleRepo.findByName("ROLE_ADMIN").get();
            admin.getRoles().add(adminRole);
            admin.getRoles().add(roleRepo.findByName("ROLE_USER").get());
            userRepo.save(admin);
            System.out.println("Created default admin: " + adminEmail + " / AdminPass123!");
        }
    }
}