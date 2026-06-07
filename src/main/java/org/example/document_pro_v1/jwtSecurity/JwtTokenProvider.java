package org.example.document_pro_v1.jwtSecurity;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.password}")
    private String password;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(Authentication authentication, String tenantId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date expirationDate = new Date(System.currentTimeMillis() + expiration * 1000);
        SecretKey secretKey = Keys.hmacShaKeyFor(password.getBytes());

        String Role = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        log.info("Generated JWT Token for User: {}", userDetails.getUsername());
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .claim("role", Role)
                .claim("tenantId", tenantId)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();


    }

    public boolean validateToken(String token) {
        try{
            SecretKey key = Keys.hmacShaKeyFor(password.getBytes());

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            log.info("Token validated successfully");
            return true;
        }
        catch (MalformedJwtException ex) {
            log.info("Invalid JWT token");
            System.err.println("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.info("Expired JWT token");
            System.err.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.info("Unsupported JWT token");
            System.err.println("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.info("JWT claims string is empty");
            System.err.println("JWT claims string is empty");
        }
        return false;

    }

    public String getUserDetailsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(password.getBytes());
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(password.getBytes());

        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.get("role", String.class);

    }

    public String getTenantIdFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(password.getBytes());
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.get("tenantId", String.class);

    }
}

