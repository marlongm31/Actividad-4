package com.barberturn.adapter.out.persistence.repository;

import com.barberturn.adapter.out.persistence.entity.AppointmentEntity;
import com.barberturn.domain.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findByDateOrderByStartTimeAsc(LocalDate date);

    List<AppointmentEntity> findByDateAndStatusOrderByStartTimeAsc(LocalDate date, AppointmentStatus status);

    List<AppointmentEntity> findAllByOrderByDateDescStartTimeAsc();
}
