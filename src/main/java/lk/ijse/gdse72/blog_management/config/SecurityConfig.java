package lk.ijse.gdse72.blog_management.config;

import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.gdse72.blog_management.service.CustomOAuth2UserService;
import lk.ijse.gdse72.blog_management.service.EmailService;
import lk.ijse.gdse72.blog_management.utility.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final EmailService emailService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, EmailService emailService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.emailService = emailService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/posts/me/**").authenticated()  // keep first
                        .requestMatchers(HttpMethod.GET, "/api/posts/published").permitAll()
                        .requestMatchers("/api/posts/**").authenticated() // require auth for other POST/PUT/DELETE
                        .requestMatchers("/admin.html", "/api/admins/**").hasRole("ADMIN")
                        .requestMatchers("/images/**", "/uploads/**", "/front_end/**").permitAll()
                        .requestMatchers("/Login.html", "/api/auth/**", "/", "/index.html", "/published-posts.html", "/my_account.html", "/post.html").permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/Login.html")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customAuthenticationSuccessHandler())
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("JWT")
                        .logoutSuccessUrl("/index.html")
                );

        return http.build();
    }

    @Bean
    public JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter() {
        return new JwtTokenAuthenticationFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:63342", "http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var oAuth2User = (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            String name  = oAuth2User.getAttribute("name");

            // Decide role (admin email → ROLE_ADMIN)
            String role = "admin@example.com".equalsIgnoreCase(email) ? "ROLE_ADMIN" : "ROLE_USER";

            // Generate JWT
            String token = JwtUtil.generateToken(email, role);

            ResponseCookie cookie = ResponseCookie.from("JWT", token)
                    .httpOnly(true)
                    .secure(false)   // true in production with HTTPS
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(60L * 60 * 24) // 1 day
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());

            // Send login email
            try { emailService.sendLoginSuccessEmail(email, name != null ? name : email); } catch (Exception ignored) {}

            response.sendRedirect("/my_account.html");
        };
    }
}
