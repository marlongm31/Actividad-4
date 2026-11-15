package com.barberturn.application.port.in.dto;

import java.time.LocalTime;

public record BusinessHoursCommand(
        int dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        boolean active
) {
}
