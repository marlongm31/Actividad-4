package com.barberturn.adapter.in.web.dto;

import java.time.LocalTime;

public record BusinessHoursResponse(
        int dayOfWeek,
        String dayName,
        LocalTime openTime,
        LocalTime closeTime,
        boolean active
) {
}
