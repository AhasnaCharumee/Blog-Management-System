package lk.ijse.gdse72.blog_management.controller;

import lk.ijse.gdse72.blog_management.dto.UserDTO;
import lk.ijse.gdse72.blog_management.entity.User;
import lk.ijse.gdse72.blog_management.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    }

    // Get current logged-in user
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return ResponseEntity.status(401).body(null);

        String email = principal.getAttribute("email");
        if (email == null) return ResponseEntity.status(401).body(null);

        return userRepository.findByEmail(email)
                .map(u -> ResponseEntity.ok(new UserDTO(
                        u.getId(),
                        u.getEmail(),
                        u.getName(),
                        u.getBio(),
                        u.getProfileImagePath()
                )))
                .orElseGet(() -> ResponseEntity.status(404).body(null));
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
}
