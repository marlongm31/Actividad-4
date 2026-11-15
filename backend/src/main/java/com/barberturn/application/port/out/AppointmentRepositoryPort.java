package com.barberturn.application.port.out;

import com.barberturn.domain.model.Appointment;
import com.barberturn.domain.model.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepositoryPort {

    Optional<Appointment> findById(Long id);

    Appointment save(Appointment appointment);

    List<Appointment> findByDate(LocalDate date);

    List<Appointment> findByDateAndStatus(LocalDate date, AppointmentStatus status);

    List<Appointment> findAll();
}
