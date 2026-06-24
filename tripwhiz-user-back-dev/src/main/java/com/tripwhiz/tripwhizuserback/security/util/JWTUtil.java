package com.tripwhiz.tripwhizuserback.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtil {

    private final SecretKey secretKey;

    public JWTUtil(@Value("${com.tripwhiz.jwt.secret}") String jwtSecret) {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("TRIPWHIZ_JWT_SECRET environment variable must be set.");
        }

        byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("TRIPWHIZ_JWT_SECRET must be at least 32 bytes for HS256.");
        }

        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
    }

    public String createToken(Map<String, Object> valueMap, int min) {

        return Jwts.builder().header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .expiration((Date.from(ZonedDateTime.now()
                        .plusMinutes(min).toInstant()))).claims(valueMap)
                .signWith(secretKey)
                .compact();

    }

    public Map<String, Object> validateToken(String token) {

        Claims claims = Jwts.parser().verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims;

    }
}
