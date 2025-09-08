package lk.ijse.gdse72.blog_management.controller;

import lk.ijse.gdse72.blog_management.dto.UserDTO;
import lk.ijse.gdse72.blog_management.entity.User;
import lk.ijse.gdse72.blog_management.repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/"; // project root uploads folder

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs(); // create folder if not exist
    } @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal Object principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = null;

        // Case 1: Spring Security JWT / UsernamePassword
        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            email = userDetails.getUsername();
        }

        // Case 2: OAuth2 Login
        if (principal instanceof OAuth2User oauth2User) {
            email = oauth2User.getAttribute("email");
        }

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return userRepository.findByEmail(email)
                .map(u -> ResponseEntity.ok(new UserDTO(
                        u.getId(),
                        u.getEmail(),
                        u.getName(),
                        u.getBio(),
                        u.getProfileImagePath()
                )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    // Update profile
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

            User user = optionalUser.get();
            user.setName(name);
            user.setEmail(email);
            user.setBio(bio);

            if (profileImage != null && !profileImage.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
                File dest = new File(UPLOAD_DIR + fileName);
                profileImage.transferTo(dest);
                user.setProfileImagePath(fileName);
            }

            userRepository.save(user);
            return ResponseEntity.ok("Profile updated successfully");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save image");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update profile");
        }
    }
    // File: src/main/java/lk/ijse/gdse72/blog_management/controller/UserController.java

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll(); // JSON list of users return wenawa
    }
    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }


}
