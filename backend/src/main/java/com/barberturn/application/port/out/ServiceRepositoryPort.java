package com.barberturn.application.port.out;

import com.barberturn.domain.model.Service;

import java.util.List;
import java.util.Optional;

public interface ServiceRepositoryPort {

    List<Service> findAll();

    List<Service> findAllActive();

    Optional<Service> findById(Long id);

    Service save(Service service);
}
