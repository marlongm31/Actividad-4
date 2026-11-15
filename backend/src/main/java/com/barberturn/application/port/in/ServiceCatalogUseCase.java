package com.barberturn.application.port.in;

import com.barberturn.application.port.in.dto.ServiceCommand;
import com.barberturn.domain.model.Service;

import java.util.List;

public interface ServiceCatalogUseCase {

    List<Service> listActiveServices();

    List<Service> listAllServices();

    Service createService(ServiceCommand command);

    Service updateService(Long id, ServiceCommand command);

    void deactivateService(Long id);
}
