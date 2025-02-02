package com.vinland.store.security.auth.service;

import com.vinland.store.web.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String BEARER_PREFIX = "Bearer ";
    @Value("${jwt.key}")
    private String accessTokenSecret;
    @Value("${jwt.accessToken}")
    private Long accessTokenExpirationMinutes;
    @Value("${jwt.refreshToken}")
    private Long refreshTokenExpirationMinutes;

    // generate access token
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, "access", accessTokenExpirationMinutes);
    }

    // generate refresh token
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, "refresh", refreshTokenExpirationMinutes);
    }

    private String generateToken(UserDetails userDetails, String tokenType, Long expirationMinutes) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("type", tokenType);
        }
        return BEARER_PREFIX + buildToken(claims, userDetails, expirationMinutes);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Long expirationMinutes) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES)))
                .id(UUID.randomUUID().toString())
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .header().type("JWT")
                .and().compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(accessTokenSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isRefreshToken(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class)).equals("refresh");
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername());
    }
}
