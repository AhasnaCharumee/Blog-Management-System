// File: src/main/java/lk/ijse/gdse72/blog_management/utility/JwtUtil.java
package lk.ijse.gdse72.blog_management.utility;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // Use a fixed secret (load from application.properties in real apps)
    private static final String SECRET = "my-super-secret-key-for-jwt-which-should-be-long-enough";
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    private static final long EXPIRATION = 1000 * 60 * 60*24*24; // 1 hour

    // Generate JWT Token
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate and extract username
    public static String validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
            return null;
        } catch (JwtException e) {
            System.out.println("Invalid token: " + e.getMessage());
            return null;
        }
    }
}
