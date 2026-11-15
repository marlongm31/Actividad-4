package com.barberturn.adapter.out.persistence.mapper;

import com.barberturn.adapter.out.persistence.entity.ServiceEntity;
import com.barberturn.domain.model.Service;

public final class ServiceMapper {

    private ServiceMapper() {
    }

    public static Service toDomain(ServiceEntity entity) {
        return new Service(entity.getId(), entity.getName(), entity.getDescription(),
                entity.getPrice(), entity.getDurationMinutes(), entity.isActive());
    }

    public static ServiceEntity toEntity(Service service) {
        return new ServiceEntity(service.id(), service.name(), service.description(),
                service.price(), service.durationMinutes(), service.active());
    }
}
