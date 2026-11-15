package com.barberturn.domain.model;

import com.barberturn.domain.exception.DomainException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record Appointment(
        Long id,
        Long serviceId,
        String serviceName,
        BigDecimal servicePrice,
        int durationMinutes,
        String customerName,
        String customerPhone,
        String customerEmail,
        String notes,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        AppointmentStatus status,
        LocalDateTime createdAt
) {

    public Appointment {
        if (date == null || startTime == null || endTime == null) {
            throw new DomainException("La fecha y hora de la cita son obligatorias");
        }
        if (!startTime.isBefore(endTime)) {
            throw new DomainException("La hora de inicio debe ser anterior a la de fin");
        }
        if (customerName == null || customerName.isBlank()) {
            throw new DomainException("El nombre del cliente es obligatorio");
        }
        if (status == null) {
            throw new DomainException("El estado de la cita es obligatorio");
        }
    }

    public static Appointment create(
            Service service,
            String customerName,
            String customerPhone,
            String customerEmail,
            String notes,
            LocalDate date,
            LocalTime startTime,
            LocalDateTime createdAt
    ) {
        LocalTime endTime = startTime.plusMinutes(service.durationMinutes());
        return new Appointment(
                null, service.id(), service.name(), service.price(), service.durationMinutes(),
                customerName.trim(), customerPhone.trim(), customerEmail, notes,
                date, startTime, endTime, AppointmentStatus.PENDING, createdAt
        );
    }

    public Appointment withId(Long newId) {
        return new Appointment(newId, serviceId, serviceName, servicePrice, durationMinutes,
                customerName, customerPhone, customerEmail, notes, date, startTime, endTime, status, createdAt);
    }

    public Appointment withStatus(AppointmentStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new DomainException(
                    "No se puede cambiar una cita de " + status.label() + " a " + newStatus.label());
        }
        return new Appointment(id, serviceId, serviceName, servicePrice, durationMinutes,
                customerName, customerPhone, customerEmail, notes, date, startTime, endTime, newStatus, createdAt);
    }

    public boolean isActive() {
        return status != AppointmentStatus.CANCELLED;
    }

    public TimeSlot slot() {
        return new TimeSlot(startTime, endTime);
    }

    public boolean overlapsWith(TimeSlot other) {
        return isActive() && slot().overlaps(other);
    }
}
