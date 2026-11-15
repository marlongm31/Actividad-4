package com.barberturn.adapter.out.persistence.repository;

import com.barberturn.adapter.out.persistence.entity.BusinessHoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessHoursJpaRepository extends JpaRepository<BusinessHoursEntity, Long> {

    Optional<BusinessHoursEntity> findByDayOfWeek(int dayOfWeek);

    List<BusinessHoursEntity> findAllByOrderByDayOfWeekAsc();
}
