package com.barberturn.application.usecase;

import com.barberturn.application.port.in.AppointmentManagementUseCase;
import com.barberturn.application.port.in.dto.DashboardStats;
import com.barberturn.application.port.out.AppointmentRepositoryPort;
import com.barberturn.domain.exception.NotFoundException;
import com.barberturn.domain.model.Appointment;
import com.barberturn.domain.model.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AppointmentManagementService implements AppointmentManagementUseCase {

    private final AppointmentRepositoryPort appointmentRepository;

    public AppointmentManagementService(AppointmentRepositoryPort appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<Appointment> searchAppointments(LocalDate date, AppointmentStatus status) {
        if (date != null && status != null) {
            return appointmentRepository.findByDateAndStatus(date, status);
        }
        if (date != null) {
            return appointmentRepository.findByDate(date);
        }
        List<Appointment> all = appointmentRepository.findAll();
        if (status == null) {
            return all;
        }
        return all.stream().filter(appointment -> appointment.status() == status).toList();
    }

    @Override
    public Appointment findAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No se encontro la cita con id " + id));
    }

    @Override
    public Appointment changeStatus(Long id, AppointmentStatus newStatus) {
        Appointment appointment = findAppointment(id);
        return appointmentRepository.save(appointment.withStatus(newStatus));
    }

    @Override
    public DashboardStats dashboard(LocalDate date) {
        List<Appointment> dayAppointments = appointmentRepository.findByDate(date);
        long pending = countByStatus(dayAppointments, AppointmentStatus.PENDING);
        long confirmed = countByStatus(dayAppointments, AppointmentStatus.CONFIRMED);
        long completed = countByStatus(dayAppointments, AppointmentStatus.COMPLETED);
        long cancelled = countByStatus(dayAppointments, AppointmentStatus.CANCELLED);
        BigDecimal revenue = dayAppointments.stream()
                .filter(Appointment::isActive)
                .map(Appointment::servicePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new DashboardStats(dayAppointments.size(), pending, confirmed, completed, cancelled, revenue);
    }

    private long countByStatus(List<Appointment> appointments, AppointmentStatus status) {
        return appointments.stream().filter(appointment -> appointment.status() == status).count();
    }
}
