package com.api_3.api_3.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(
        @Value("${jwt.secret:dev-secret-change}") String secret,
        @Value("${jwt.expiration-ms:604800000}") long expirationMs
    ) {
        // For jjwt 0.11.x, we can build key from bytes; in production prefer Base64-encoded key
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException ex) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            boolean valid = username != null && username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token);
            if (!valid) {
                log.debug("JWT inválido para usuário {}. sujeito extraído={} expirada={}",
                        userDetails.getUsername(), username, isTokenExpired(token));
            }
            return valid;
        } catch (Exception ex) {
            log.warn("Falha ao validar token JWT", ex);
            return false;
        }
    }

    // Invite tokens for TeamsController
    public String generateInviteToken(String teamUuid) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + 24 * 60 * 60 * 1000L); // 24h
        return Jwts.builder()
                .claim("invite_team", teamUuid)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractInviteTeamId(String token) throws ExpiredJwtException {
        Claims claims = extractAllClaims(token);
        Object v = claims.get("invite_team");
        return v != null ? v.toString() : null;
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration != null && expiration.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
