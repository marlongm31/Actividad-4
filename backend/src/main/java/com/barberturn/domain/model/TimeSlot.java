package com.barberturn.domain.model;

public record TimeSlot(java.time.LocalTime start, java.time.LocalTime end) {

    public boolean overlaps(TimeSlot other) {
        return start.isBefore(other.end) && end.isAfter(other.start);
    }
}
