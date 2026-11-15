package com.barberturn.application.port.in.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookAppointmentCommand(
        Long serviceId,
        LocalDate date,
        LocalTime startTime,
        String customerName,
        String customerPhone,
        String customerEmail,
        String notes
) {
}
