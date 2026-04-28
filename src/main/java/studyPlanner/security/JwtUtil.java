package studyPlanner.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;

/*
- creates a JWT token when user logs in 
- verifies a JWT token on every protected request
- extracts the email from token
*/

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String SECRET;
    private final long EXPIRATION = 1000 * 60 * 60 * 24; // 24 hours in milliseconds

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Called at login — creates and returns a token
    public String generateToken(String email) {
        return Jwts.builder() // start building a token
                .subject(email) // WHO this token belongs to
                .issuedAt(new Date()) // WHEN it was created (right now)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION)) // WHEN it expires
                .signWith(getSigningKey()) // SIGN it with our secret
                .compact(); // convert to the xxxxx.yyyyy.zzzzz string
    }

    // Extracts the email from a token
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // Checks if token is valid and not expired
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Parses the token and returns the payload (claims)
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
