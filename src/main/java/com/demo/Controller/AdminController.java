package com.demo.Controller;

import com.demo.Model.Role;
import com.demo.Model.Status;
import com.demo.Model.User;
import com.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(
    origins = "http://106.102.1.60",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    // Liste des utilisateurs en attente
    @GetMapping("/pending-users")
    public ResponseEntity<List<User>> getPendingUsers() {
        return ResponseEntity.ok(userRepository.findByStatus(Status.PENDING));
    }

    // Approuver et assigner un rôle
    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approveUser(@PathVariable Long id, @RequestParam String role) {
        return userRepository.findById(id).map(user -> {
            user.setStatus(Status.APPROVED);
            user.setRole(Role.valueOf(role));
            userRepository.save(user);
            return ResponseEntity.ok("User approved with role " + role);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Rejeter un utilisateur
    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectUser(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            user.setStatus(Status.REJECTED);
            userRepository.save(user);
            return ResponseEntity.ok("User rejected");
        }).orElse(ResponseEntity.notFound().build());
    }
    // Tous les utilisateurs
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Changer le rôle
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestParam String role) {
        return userRepository.findById(id).map(user -> {
            user.setRole(Role.valueOf(role));
            userRepository.save(user);
            return ResponseEntity.ok("Role updated");
        }).orElse(ResponseEntity.notFound().build());
    }

    // Changer le statut
    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return userRepository.findById(id).map(user -> {
            user.setStatus(Status.valueOf(status));
            userRepository.save(user);
            return ResponseEntity.ok("Status updated");
        }).orElse(ResponseEntity.notFound().build());
    }

    // Supprimer
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted");
    }
}