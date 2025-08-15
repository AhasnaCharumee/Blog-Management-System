package lk.ijse.gdse72.blog_management.controller;

import lk.ijse.gdse72.blog_management.dto.UserDTO;
import lk.ijse.gdse72.blog_management.repository.UserRepository;
import lk.ijse.gdse72.blog_management.entity.User;
import lk.ijse.gdse72.blog_management.utility.APIResponse;
import org.springframework.http.ResponseEntity; // <-- Add this import
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
// src/main/java/lk/ijse/gdse72/blog_management/controller/UserController.java
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(null);
        }
        String email = principal.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(401).body(null);
        }
        return userRepository.findByEmail(email)
                .map(u -> ResponseEntity.ok(new UserDTO(u.getId(), u.getEmail(), u.getName())))
                .orElseGet(() -> ResponseEntity.status(404).body(null));
    }
}
