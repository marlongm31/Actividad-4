package com.barberturn.domain.model;

import java.util.EnumSet;
import java.util.Set;

public enum AppointmentStatus {
    PENDING("Pendiente"),
    CONFIRMED("Confirmada"),
    CANCELLED("Cancelada"),
    COMPLETED("Completada");

    private final String label;

    AppointmentStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public boolean canTransitionTo(AppointmentStatus target) {
        if (target == null || target == this) {
            return false;
        }
        return switch (this) {
            case PENDING -> EnumSet.of(CONFIRMED, CANCELLED).contains(target);
            case CONFIRMED -> EnumSet.of(COMPLETED, CANCELLED).contains(target);
            case CANCELLED, COMPLETED -> false;
        };
    }

    public boolean isFinal() {
        return Set.of(CANCELLED, COMPLETED).contains(this);
    }
}
