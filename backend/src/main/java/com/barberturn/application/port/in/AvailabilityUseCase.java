package com.barberturn.application.port.in;

import com.barberturn.domain.model.TimeSlot;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityUseCase {

    List<TimeSlot> availableSlots(LocalDate date, Long serviceId);
}
