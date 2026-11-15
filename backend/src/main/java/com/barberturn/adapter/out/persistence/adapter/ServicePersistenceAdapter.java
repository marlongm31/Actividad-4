package com.barberturn.adapter.out.persistence.adapter;

import com.barberturn.adapter.out.persistence.mapper.ServiceMapper;
import com.barberturn.adapter.out.persistence.repository.ServiceJpaRepository;
import com.barberturn.application.port.out.ServiceRepositoryPort;
import com.barberturn.domain.model.Service;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ServicePersistenceAdapter implements ServiceRepositoryPort {

    private final ServiceJpaRepository repository;

    public ServicePersistenceAdapter(ServiceJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Service> findAll() {
        return repository.findAllByOrderByActiveDescNameAsc().stream()
                .map(ServiceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Service> findAllActive() {
        return repository.findByActiveTrueOrderByNameAsc().stream()
                .map(ServiceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Service> findById(Long id) {
        return repository.findById(id).map(ServiceMapper::toDomain);
    }

    @Override
    public Service save(Service service) {
        return ServiceMapper.toDomain(repository.save(ServiceMapper.toEntity(service)));
    }
}
