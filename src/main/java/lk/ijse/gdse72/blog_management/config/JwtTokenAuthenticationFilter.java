package lk.ijse.gdse72.blog_management.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String ADMIN_TOKEN = "supersecretadmintoken123"; // Hardcoded for demo; replace with JWT validation in production

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // Remove "Bearer " prefix

            System.out.println("Received token: " + token); // Debug log

            if (token != null && ADMIN_TOKEN.equals(token)) {
                System.out.println("Token validated successfully for admin"); // Debug log
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        "admin", // Principal
                        null,    // Credentials
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                System.out.println("Invalid or missing token"); // Debug log
            }
        } else {
            System.out.println("No Authorization header or invalid format"); // Debug log
        }

        filterChain.doFilter(request, response); // Always continue the chain
    }
}