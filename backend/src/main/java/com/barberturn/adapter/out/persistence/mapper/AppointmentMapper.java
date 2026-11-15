package com.barberturn.adapter.out.persistence.mapper;

import com.barberturn.adapter.out.persistence.entity.AppointmentEntity;
import com.barberturn.domain.model.Appointment;

public final class AppointmentMapper {

    private AppointmentMapper() {
    }

    public static Appointment toDomain(AppointmentEntity entity) {
        return new Appointment(
                entity.getId(),
                entity.getServiceId(),
                entity.getServiceName(),
                entity.getServicePrice(),
                entity.getDurationMinutes(),
                entity.getCustomerName(),
                entity.getCustomerPhone(),
                entity.getCustomerEmail(),
                entity.getNotes(),
                entity.getDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getStatus(),
                entity.getCreatedAt());
    }

    public static AppointmentEntity toEntity(Appointment appointment) {
        return new AppointmentEntity(
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
                appointment.status(),
                appointment.createdAt());
    }
}
