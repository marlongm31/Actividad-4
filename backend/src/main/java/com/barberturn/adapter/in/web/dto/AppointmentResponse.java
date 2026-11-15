package com.barberturn.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AppointmentResponse(
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
        String status,
        String statusLabel,
        LocalDateTime createdAt
) {
}
