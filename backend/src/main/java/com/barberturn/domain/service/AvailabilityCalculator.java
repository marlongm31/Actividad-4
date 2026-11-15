package com.barberturn.domain.service;

import com.barberturn.domain.exception.BusinessRuleException;
import com.barberturn.domain.exception.ConflictException;
import com.barberturn.domain.model.Appointment;
import com.barberturn.domain.model.BusinessHours;
import com.barberturn.domain.model.Service;
import com.barberturn.domain.model.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de dominio puro: calcula la disponibilidad y valida las reglas de
 * agendamiento sin conocer base de datos, HTTP ni frameworks.
 */
public final class AvailabilityCalculator {

    public static final int SLOT_INTERVAL_MINUTES = 15;

    public List<TimeSlot> availableSlots(
            LocalDate date,
            BusinessHours hours,
            Service service,
            List<Appointment> dayAppointments,
            LocalDateTime now
    ) {
        if (hours == null || !hours.active() || service == null || !service.active()) {
            return List.of();
        }

        List<TimeSlot> slots = new ArrayList<>();
        LocalTime candidate = hours.openTime();
        LocalTime closing = hours.closeTime();
        LocalTime lastStart = closing.minusMinutes(service.durationMinutes());

        while (!candidate.isAfter(lastStart)) {
            TimeSlot slot = new TimeSlot(candidate, candidate.plusMinutes(service.durationMinutes()));
            boolean isFuture = date.atTime(candidate).isAfter(now);
            boolean isFree = dayAppointments.stream().noneMatch(appointment -> appointment.overlapsWith(slot));
            if (isFuture && isFree) {
                slots.add(slot);
            }
            candidate = candidate.plusMinutes(SLOT_INTERVAL_MINUTES);
        }
        return slots;
    }

    public void validateBooking(
            LocalDate date,
            LocalTime startTime,
            Service service,
            BusinessHours hours,
            List<Appointment> dayAppointments,
            LocalDateTime now
    ) {
        if (service == null || !service.active()) {
            throw new BusinessRuleException("El servicio seleccionado no esta disponible");
        }
        if (date.atTime(startTime).isBefore(now)) {
            throw new BusinessRuleException("No se pueden agendar citas en fechas u horas pasadas");
        }
        if (hours == null || !hours.active()) {
            throw new BusinessRuleException("La peluqueria no atiende el dia seleccionado");
        }

        LocalTime endTime = startTime.plusMinutes(service.durationMinutes());
        if (startTime.isBefore(hours.openTime()) || endTime.isAfter(hours.closeTime())) {
            throw new BusinessRuleException(
                    "El horario seleccionado esta fuera del horario de atencion ("
                            + hours.openTime() + " - " + hours.closeTime() + ")");
        }

        TimeSlot requested = new TimeSlot(startTime, endTime);
        boolean overlaps = dayAppointments.stream().anyMatch(appointment -> appointment.overlapsWith(requested));
        if (overlaps) {
            throw new ConflictException("El horario seleccionado ya fue reservado. Elige otro horario.");
        }
    }
}
