package com.barberturn.application.port.out;

import java.util.Optional;

public interface TokenProviderPort {

    String createToken(String subject);

    Optional<String> extractSubject(String token);

    long expirationSeconds();
}
