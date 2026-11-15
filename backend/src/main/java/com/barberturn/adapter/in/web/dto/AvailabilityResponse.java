package com.barberturn.adapter.in.web.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AvailabilityResponse(
        LocalDate date,
        Long serviceId,
        List<Slot> slots
) {

    public record Slot(LocalTime startTime, LocalTime endTime) {
    }
}
