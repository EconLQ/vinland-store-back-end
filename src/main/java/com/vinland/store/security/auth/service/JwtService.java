package com.vinland.store.security.auth.service;

import com.vinland.store.web.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
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
    private Integer accessTokenExpirationSeconds;
    @Value("${jwt.refreshToken}")
    private Integer refreshTokenExpirationSeconds;

    // generate access token
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, "access", accessTokenExpirationSeconds);
    }

    // generate refresh token
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, "refresh", refreshTokenExpirationSeconds);
    }

    private String generateToken(UserDetails userDetails, String tokenType, Integer expirationSeconds) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User) {
            claims.put("type", tokenType);
        }
        return BEARER_PREFIX + buildToken(claims, userDetails, expirationSeconds);
    }

    public Cookie createCookieWithJwt(String token, String tokenName) {
        Cookie cookie = new Cookie(tokenName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setMaxAge(refreshTokenExpirationSeconds);
        cookie.setPath("/");
        return cookie;
    }

    public Cookie removeCookieWithJwt(String tokenName) {
        Cookie refreshCookie = new Cookie(tokenName, null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setAttribute("SameSite", "None");
        refreshCookie.setMaxAge(0);
        refreshCookie.setPath("/");
        return refreshCookie;
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Integer expirationSeconds) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(expirationSeconds, ChronoUnit.SECONDS)))
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
