package com.barberturn.adapter.out.persistence.mapper;

import com.barberturn.adapter.out.persistence.entity.BusinessHoursEntity;
import com.barberturn.domain.model.BusinessHours;

public final class BusinessHoursMapper {

    private BusinessHoursMapper() {
    }

    public static BusinessHours toDomain(BusinessHoursEntity entity) {
        return new BusinessHours(entity.getId(), entity.getDayOfWeek(),
                entity.getOpenTime(), entity.getCloseTime(), entity.isActive());
    }

    public static BusinessHoursEntity toEntity(BusinessHours hours) {
        return new BusinessHoursEntity(hours.id(), hours.dayOfWeek(),
                hours.openTime(), hours.closeTime(), hours.active());
    }
}
