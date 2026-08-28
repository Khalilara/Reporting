package com.demo.Service;

import com.demo.Model.User;
import com.demo.Repository.UserRepository;
import com.demo.dto.LoginRequest;
import com.demo.dto.RegisterRequest;
import com.demo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import com.demo.Model.Role;
import com.demo.Model.Status;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    public void initAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setEmail("admin@reporting.com");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setPhoneNumber("0000000000");
            adminUser.setFonction("Admin");
            adminUser.setCreatedAt(LocalDateTime.now());
            adminUser.setUpdatedAt(LocalDateTime.now());
            userRepository.save(adminUser);
        }
    }
    
    public User register(RegisterRequest request) throws Exception {
    if (userRepository.existsByUsername(request.getUsername())) {
        throw new Exception("Username already exists");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new Exception("Email already exists");
    }
    
    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setEmail(request.getEmail());
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setPhoneNumber(request.getPhoneNumber());
    user.setFonction(request.getFonction());
    user.setStatus(Status.PENDING); // ← ajout
    user.setRole(Role.CHANNEL);     // rôle par défaut, sera écrasé par SUPER_ADMIN
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    
    return userRepository.save(user);
}
    
    public User login(LoginRequest request) throws Exception {
    Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
    
    if (!userOptional.isPresent()) {
        throw new Exception("User not found");
    }
    
    User user = userOptional.get();
    
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new Exception("Invalid password");
    }
    
    if (user.getStatus() != Status.APPROVED) {
        if (user.getStatus() == Status.PENDING) {
            throw new Exception("Your account is pending approval by an administrator");
        }
        if (user.getStatus() == Status.REJECTED) {
            throw new Exception("Your account has been rejected");
        }
    }
    
    return user;
}
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
