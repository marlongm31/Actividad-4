package com.barberturn.adapter.in.web;

import com.barberturn.adapter.in.web.dto.ServiceRequest;
import com.barberturn.adapter.in.web.dto.ServiceResponse;
import com.barberturn.adapter.in.web.mapper.ServiceWebMapper;
import com.barberturn.application.port.in.ServiceCatalogUseCase;
import com.barberturn.application.port.in.dto.ServiceCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/services")
public class AdminServiceController {

    private final ServiceCatalogUseCase serviceCatalogUseCase;

    public AdminServiceController(ServiceCatalogUseCase serviceCatalogUseCase) {
        this.serviceCatalogUseCase = serviceCatalogUseCase;
    }

    @GetMapping
    public List<ServiceResponse> list() {
        return serviceCatalogUseCase.listAllServices().stream()
                .map(ServiceWebMapper::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> create(@Valid @RequestBody ServiceRequest request) {
        var created = serviceCatalogUseCase.createService(toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(ServiceWebMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ServiceResponse update(@PathVariable Long id, @Valid @RequestBody ServiceRequest request) {
        return ServiceWebMapper.toResponse(serviceCatalogUseCase.updateService(id, toCommand(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        serviceCatalogUseCase.deactivateService(id);
        return ResponseEntity.noContent().build();
    }

    private ServiceCommand toCommand(ServiceRequest request) {
        return new ServiceCommand(request.name(), request.description(),
                request.price(), request.durationMinutes(), request.active());
    }
}
