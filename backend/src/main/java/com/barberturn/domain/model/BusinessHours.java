package com.barberturn.domain.model;

import java.time.LocalTime;

public record BusinessHours(
        Long id,
        int dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        boolean active
) {

    public BusinessHours {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("El dia de la semana debe estar entre 1 y 7");
        }
        if (openTime == null || closeTime == null) {
            throw new IllegalArgumentException("Debe indicar la hora de apertura y de cierre");
        }
        if (!openTime.isBefore(closeTime)) {
            throw new IllegalArgumentException("La hora de apertura debe ser anterior a la de cierre");
        }
    }

    public BusinessHours withId(Long newId) {
        return new BusinessHours(newId, dayOfWeek, openTime, closeTime, active);
    }

    public BusinessHours update(LocalTime newOpen, LocalTime newClose, boolean newActive) {
        return new BusinessHours(id, dayOfWeek, newOpen, newClose, newActive);
    }
}
