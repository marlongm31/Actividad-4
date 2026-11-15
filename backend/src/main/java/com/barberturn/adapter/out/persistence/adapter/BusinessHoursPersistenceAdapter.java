package com.barberturn.adapter.out.persistence.adapter;

import com.barberturn.adapter.out.persistence.mapper.BusinessHoursMapper;
import com.barberturn.adapter.out.persistence.repository.BusinessHoursJpaRepository;
import com.barberturn.application.port.out.BusinessHoursRepositoryPort;
import com.barberturn.domain.model.BusinessHours;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BusinessHoursPersistenceAdapter implements BusinessHoursRepositoryPort {

    private final BusinessHoursJpaRepository repository;

    public BusinessHoursPersistenceAdapter(BusinessHoursJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BusinessHours> findAll() {
        return repository.findAllByOrderByDayOfWeekAsc().stream()
                .map(BusinessHoursMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<BusinessHours> findByDayOfWeek(int dayOfWeek) {
        return repository.findByDayOfWeek(dayOfWeek).map(BusinessHoursMapper::toDomain);
    }

    @Override
    public BusinessHours save(BusinessHours businessHours) {
        return BusinessHoursMapper.toDomain(repository.save(BusinessHoursMapper.toEntity(businessHours)));
    }
}
