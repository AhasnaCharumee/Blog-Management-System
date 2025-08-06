package lk.ijse.gdse72.blog_management.controller;

import lk.ijse.gdse72.blog_management.dto.UserDTO;
import lk.ijse.gdse72.blog_management.repository.UserRepository;
import lk.ijse.gdse72.blog_management.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{email}")
    public UserDTO getUserByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findById(email);
        return user.map(u -> new UserDTO(u.getEmail(), u.getName())).orElse(null);
    }
}