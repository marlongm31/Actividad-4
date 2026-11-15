package com.barberturn.adapter.out.persistence.adapter;

import com.barberturn.adapter.out.persistence.mapper.AppointmentMapper;
import com.barberturn.adapter.out.persistence.repository.AppointmentJpaRepository;
import com.barberturn.application.port.out.AppointmentRepositoryPort;
import com.barberturn.domain.model.Appointment;
import com.barberturn.domain.model.AppointmentStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class AppointmentPersistenceAdapter implements AppointmentRepositoryPort {

    private final AppointmentJpaRepository repository;

    public AppointmentPersistenceAdapter(AppointmentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Appointment> findById(Long id) {
        return repository.findById(id).map(AppointmentMapper::toDomain);
    }

    @Override
    public Appointment save(Appointment appointment) {
        return AppointmentMapper.toDomain(repository.save(AppointmentMapper.toEntity(appointment)));
    }

    @Override
    public List<Appointment> findByDate(LocalDate date) {
        return repository.findByDateOrderByStartTimeAsc(date).stream()
                .map(AppointmentMapper::toDomain)
                .toList();
    }

    @Override
    public List<Appointment> findByDateAndStatus(LocalDate date, AppointmentStatus status) {
        return repository.findByDateAndStatusOrderByStartTimeAsc(date, status).stream()
                .map(AppointmentMapper::toDomain)
                .toList();
    }

    @Override
    public List<Appointment> findAll() {
        return repository.findAllByOrderByDateDescStartTimeAsc().stream()
                .map(AppointmentMapper::toDomain)
                .toList();
    }
}
