package com.barberturn.adapter.out.security;

import com.barberturn.application.port.out.TokenProviderPort;
import com.barberturn.infrastructure.config.JwtProperties;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenAdapter implements TokenProviderPort {

    private final SecretKey signingKey;
    private final long expirationMillis;

    public JwtTokenAdapter(JwtProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = properties.expirationMinutes() * 60_000L;
    }

    @Override
    public String createToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public Optional<String> extractSubject(String token) {
        try {
            return Optional.of(Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject());
        } catch (JwtException | IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    @Override
    public long expirationSeconds() {
        return expirationMillis / 1000;
    }
}
