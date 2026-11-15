package com.barberturn.application.port.in.dto;

public record AuthResult(String token, String name, String email, long expiresInSeconds) {
}
