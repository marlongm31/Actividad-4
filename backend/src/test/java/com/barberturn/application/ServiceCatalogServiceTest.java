package com.barberturn.application;

import com.barberturn.application.port.in.dto.ServiceCommand;
import com.barberturn.application.port.out.ServiceRepositoryPort;
import com.barberturn.application.usecase.ServiceCatalogService;
import com.barberturn.domain.exception.DomainException;
import com.barberturn.domain.exception.NotFoundException;
import com.barberturn.domain.model.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceCatalogServiceTest {

    @Mock
    private ServiceRepositoryPort repository;

    private ServiceCatalogService service;

    @BeforeEach
    void setUp() {
        service = new ServiceCatalogService(repository);
    }

    @Test
    void createsAServiceWithTheCommandData() {
        ServiceCommand command = new ServiceCommand(
                "Corte premium", "Corte y peinado", new BigDecimal("30000"), 45, true);
        when(repository.save(any(Service.class)))
                .thenAnswer(invocation -> invocation.<Service>getArgument(0).withId(7L));

        Service created = service.createService(command);

        assertThat(created.id()).isEqualTo(7L);
        assertThat(created.name()).isEqualTo("Corte premium");
        assertThat(created.price()).isEqualByComparingTo("30000");
        assertThat(created.active()).isTrue();
    }

    @Test
    void rejectsAServiceWithInvalidDuration() {
        ServiceCommand command = new ServiceCommand(
                "Corte", "Corte", new BigDecimal("30000"), 5, true);

        assertThatThrownBy(() -> service.createService(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("duracion");
    }

    @Test
    void deactivatesAnExistingService() {
        Service existing = new Service(3L, "Corte", "Corte", new BigDecimal("25000"), 45, true);
        when(repository.findById(3L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Service.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.deactivateService(3L);

        verify(repository).save(any(Service.class));
    }

    @Test
    void throwsWhenUpdatingAnUnknownService() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateService(99L,
                new ServiceCommand("Corte", "Corte", new BigDecimal("25000"), 45, true)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void listsActiveServicesFromTheRepository() {
        when(repository.findAllActive()).thenReturn(List.of(
                new Service(1L, "Corte", "Corte", new BigDecimal("25000"), 45, true)));

        assertThat(service.listActiveServices()).hasSize(1);
    }
}
