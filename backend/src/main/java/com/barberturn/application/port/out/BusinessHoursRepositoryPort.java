package com.barberturn.application.port.out;

import com.barberturn.domain.model.BusinessHours;

import java.util.List;
import java.util.Optional;

public interface BusinessHoursRepositoryPort {

    List<BusinessHours> findAll();

    Optional<BusinessHours> findByDayOfWeek(int dayOfWeek);

    BusinessHours save(BusinessHours businessHours);
}
