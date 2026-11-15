package com.barberturn.domain.model;

public record AdminUser(
        Long id,
        String email,
        String passwordHash,
        String name
) {
}
