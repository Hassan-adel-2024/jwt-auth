package auth.jwt.service;

import auth.jwt.entity.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}") // Ensure this is a strong, long secret key
    private String secretKey;
    @Value("${jwt.expiration}")
    private Long expiration;

    /*
       Method to generate token accepts UserDetails or any implementation of it
       Creates a map to take the role (authority) to delegate the actual generation logic for token generation
     */
    public String generateToken(AppUser appUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", appUser.getAuthorities()); // Store user roles in claims
        return createToken(claims, appUser.getEmail());
    }

    /*
     * This method generates a JWT token.
     *
     * It uses the Jwts.builder() to construct the JWT token by passing in the necessary claims, subject (email),
     * and expiration time. It also signs the token with the specified signature algorithm (HS256) and the secret key.
     *
     * - claims: A map that contains custom claims (e.g., role, user ID, etc.). This allows you to include additional
     *           information in the token payload.
     * - email: The subject of the JWT, which is typically the user's email or username.
     *
     * The expiration time is set by adding the value of 'expiration' (configured in the application properties)
     * to the current time in milliseconds.
     *
     * The token is then signed using the HS256 algorithm with the secret key, ensuring that the token is secure.
     *
     * Finally, it returns the compacted JWT string that can be used for authentication and authorization purposes.
     */
    private String createToken(Map<String, Object> claims, String email) {
        // Build the JWT token using the new API (HS256 with a key)
        return Jwts.builder()
                .setClaims(claims) // Set custom claims (e.g., roles)
                .setSubject(email) // Set the subject to the user's email
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set the issue date
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Set the expiration
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256) // Use the new signing method
                .compact(); // Generate and return the compact JWT token
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // Use the new verification API (matching signing and verification method)
        return Jwts.parser()  // Use the parser builder instead of the old parser method
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())) // Verify with the same key
                .build() // Build the parser
                .parseClaimsJws(token) // Parse the token
                .getBody(); // Return the claims
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, AppUser appUser) {
        final String username = extractUsername(token);
        return (username.equals(appUser.getEmail()) && !isTokenExpired(token)); // Fixed: Token must not be expired
    }

}
