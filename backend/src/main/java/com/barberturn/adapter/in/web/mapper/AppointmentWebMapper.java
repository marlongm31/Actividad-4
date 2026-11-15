package com.barberturn.adapter.in.web.mapper;

import com.barberturn.adapter.in.web.dto.AppointmentResponse;
import com.barberturn.domain.model.Appointment;

public final class AppointmentWebMapper {

    private AppointmentWebMapper() {
    }

    public static AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.id(),
                appointment.serviceId(),
                appointment.serviceName(),
                appointment.servicePrice(),
                appointment.durationMinutes(),
                appointment.customerName(),
                appointment.customerPhone(),
                appointment.customerEmail(),
                appointment.notes(),
                appointment.date(),
                appointment.startTime(),
                appointment.endTime(),
                appointment.status().name(),
                appointment.status().label(),
                appointment.createdAt());
    }
}
