package lk.ijse.gdse72.blog_management.utility;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET = "my-super-secret-key-for-jwt-which-should-be-long-enough-512-bits-please!";
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private static final long EXPIRATION = 1000L * 60 * 60 * 24; // 24 hours

    public static String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .addClaims(Map.of("role", role))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Jws<Claims> parseJws(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public static String getEmail(String token) {
        try {
            return parseJws(token).getBody().getSubject();
        } catch (JwtException ex) {
            return null;
        }
    }

    public static String getRole(String token) {
        try {
            Object r = parseJws(token).getBody().get("role");
            return r != null ? r.toString() : null;
        } catch (JwtException ex) {
            return null;
        }
    }

    // ✅ Add this method
    public static boolean validateToken(String token) {
        try {
            parseJws(token); // will throw exception if invalid
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
