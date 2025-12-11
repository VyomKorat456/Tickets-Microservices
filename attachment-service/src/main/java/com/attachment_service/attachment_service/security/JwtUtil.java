package com.attachment_service.attachment_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SignatureException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    // ----- extract methods used by all microservices -----

    public String extractUsername(String token) {
        try {
            String username = extractAllClaims(token).getSubject();
            log.debug("✓ Extracted username: {}", username);
            return username;
        } catch (Exception e) {
            log.error("❌ Error extracting username: {}", e.getMessage());
            return null;
        }
    }

    public Long extractUserId(String token) {
        try {
            Object userId = extractAllClaims(token).get("userId");
            Long result = (userId != null) ? Long.valueOf(userId.toString()) : null;
            log.debug("✓ Extracted userId: {}", result);
            return result;
        } catch (Exception e) {
            log.error("❌ Error extracting userId: {}", e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        try {
            Object roles = extractAllClaims(token).get("roles");
            List<String> result = roles instanceof List ? (List<String>) roles : List.of();
            log.debug("✓ Extracted roles: {}", result);
            return result;
        } catch (Exception e) {
            log.error("❌ Error extracting roles: {}", e.getMessage());
            return List.of();
        }
    }

    public boolean isTokenValid(String token) {
        try {
            log.info("=== JWT VALIDATION ===");
            log.debug("Token length: {}", token.length());
            log.debug("Secret length: {}", secret.length());
            log.debug("Secret (first 20 chars): {}", secret.substring(0, Math.min(20, secret.length())) + "...");
            
            Claims claims = extractAllClaims(token);
            log.info("✓ JWT signature verified successfully");
            log.debug("Subject: {}", claims.getSubject());
            log.debug("Issued At: {}", new Date(claims.getIssuedAt().getTime()));
            log.debug("Expiration: {}", new Date(claims.getExpiration().getTime()));
            
            return true;
        } catch (ExpiredJwtException e) {
            log.error("❌ JWT Token expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("❌ JWT Token format unsupported: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("❌ Invalid JWT Token format: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.error("❌ JWT validation error: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("❌ Unexpected error during JWT validation: {}", e.getMessage(), e);
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        try {
            log.debug("Decoding BASE64 secret key...");
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            log.debug("✓ Secret decoded to {} bytes", keyBytes.length);
            Key key = Keys.hmacShaKeyFor(keyBytes);
            log.debug("✓ HMAC-SHA key created");
            return key;
        } catch (Exception e) {
            log.error("❌ Error creating signing key: {}", e.getMessage());
            throw new RuntimeException("Failed to create signing key", e);
        }
    }

    // ----- generate token (used only in auth-service, optional in others) -----

    public String generateToken(String username, Long userId, List<String> roles, long expirationMillis) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of(
                        "userId", userId,
                        "roles", roles
                ))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSignInKey())
                .compact();
    }
}
