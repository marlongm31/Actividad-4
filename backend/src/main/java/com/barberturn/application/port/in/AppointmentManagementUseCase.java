package com.barberturn.application.port.in;

import com.barberturn.application.port.in.dto.DashboardStats;
import com.barberturn.domain.model.Appointment;
import com.barberturn.domain.model.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentManagementUseCase {

    List<Appointment> searchAppointments(LocalDate date, AppointmentStatus status);

    Appointment findAppointment(Long id);

    Appointment changeStatus(Long id, AppointmentStatus newStatus);

    DashboardStats dashboard(LocalDate date);
}
