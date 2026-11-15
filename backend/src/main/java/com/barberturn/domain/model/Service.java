package com.barberturn.domain.model;

import com.barberturn.domain.exception.DomainException;

import java.math.BigDecimal;

public record Service(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int durationMinutes,
        boolean active
) {

    public Service {
        if (name == null || name.isBlank()) {
            throw new DomainException("El nombre del servicio es obligatorio");
        }
        if (price == null || price.signum() < 0) {
            throw new DomainException("El precio del servicio no puede ser negativo");
        }
        if (durationMinutes < 15 || durationMinutes > 240) {
            throw new DomainException("La duracion del servicio debe estar entre 15 y 240 minutos");
        }
    }

    public static Service create(String name, String description, BigDecimal price, int durationMinutes) {
        return new Service(null, name, description, price, durationMinutes, true);
    }

    public Service withId(Long newId) {
        return new Service(newId, name, description, price, durationMinutes, active);
    }

    public Service update(String newName, String newDescription, BigDecimal newPrice, int newDuration, boolean newActive) {
        return new Service(id, newName, newDescription, newPrice, newDuration, newActive);
    }

    public Service deactivate() {
        return new Service(id, name, description, price, durationMinutes, false);
    }
}
