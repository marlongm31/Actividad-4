package com.barberturn.application.usecase;

import com.barberturn.application.port.in.ServiceCatalogUseCase;
import com.barberturn.application.port.in.dto.ServiceCommand;
import com.barberturn.application.port.out.ServiceRepositoryPort;
import com.barberturn.domain.exception.NotFoundException;
import com.barberturn.domain.model.Service;

import java.util.List;

public class ServiceCatalogService implements ServiceCatalogUseCase {

    private final ServiceRepositoryPort serviceRepository;

    public ServiceCatalogService(ServiceRepositoryPort serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<Service> listActiveServices() {
        return serviceRepository.findAllActive();
    }

    @Override
    public List<Service> listAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public Service createService(ServiceCommand command) {
        Service service = Service.create(
                command.name(),
                command.description(),
                command.price(),
                command.durationMinutes());
        return serviceRepository.save(service);
    }

    @Override
    public Service updateService(Long id, ServiceCommand command) {
        Service current = findService(id);
        boolean active = command.active() == null ? current.active() : command.active();
        Service updated = current.update(
                command.name(),
                command.description(),
                command.price(),
                command.durationMinutes(),
                active);
        return serviceRepository.save(updated);
    }

    @Override
    public void deactivateService(Long id) {
        Service current = findService(id);
        serviceRepository.save(current.deactivate());
    }

    private Service findService(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No se encontro el servicio con id " + id));
    }
}
