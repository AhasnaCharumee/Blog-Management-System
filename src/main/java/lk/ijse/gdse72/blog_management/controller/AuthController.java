package lk.ijse.gdse72.blog_management.controller;

import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.gdse72.blog_management.utility.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Hardcoded admin credentials for this example. In a real app,
    // you would get these from a database.
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String ADMIN_PASSWORD = "password123";
    private static final String ADMIN_TOKEN = "supersecretadmintoken123";

    @PostMapping("/login")
    public ResponseEntity<APIResponse<String>> login(@RequestBody Map<String, String> loginRequest, HttpServletResponse response) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        System.out.println("Received login request for email: " + email); // Add logging for debugging

        if (email == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(400, "Email and password are required", null));
        }

        if (ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password)) {
            // Set token in response header or cookie
            response.addHeader("Authorization", "Bearer " + ADMIN_TOKEN);
            return ResponseEntity.ok(new APIResponse<>(200, "Login successful", ADMIN_TOKEN));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(401, "Invalid email or password", null));
        }
    }
}