package lk.ijse.gdse72.blog_management.controller;

import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.gdse72.blog_management.utility.APIResponse;
import lk.ijse.gdse72.blog_management.utility.JwtUtil;
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

    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String ADMIN_PASSWORD = "password123";
    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<APIResponse<String>> login(@RequestBody Map<String, String> loginRequest, HttpServletResponse response) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        if (ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password)) {
            String jwtToken = JwtUtil.generateToken(email);
            response.addHeader("Authorization", "Bearer " + jwtToken);
            return ResponseEntity.ok(new APIResponse<>(200, "Login successful", jwtToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new APIResponse<>(401, "Invalid email or password", null));
        }
    }
}