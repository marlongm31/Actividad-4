package com.barberturn.adapter.in.web.mapper;

import com.barberturn.adapter.in.web.dto.ServiceResponse;
import com.barberturn.domain.model.Service;

public final class ServiceWebMapper {

    private ServiceWebMapper() {
    }

    public static ServiceResponse toResponse(Service service) {
        return new ServiceResponse(service.id(), service.name(), service.description(),
                service.price(), service.durationMinutes(), service.active());
    }
}
