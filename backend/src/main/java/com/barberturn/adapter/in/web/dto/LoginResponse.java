package com.barberturn.adapter.in.web.dto;

public record LoginResponse(String token, String name, String email, long expiresInSeconds) {
}
