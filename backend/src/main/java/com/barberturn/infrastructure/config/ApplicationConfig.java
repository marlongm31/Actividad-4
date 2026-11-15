package com.barberturn.infrastructure.config;

import com.barberturn.application.port.in.AuthUseCase;
import com.barberturn.application.port.in.AppointmentManagementUseCase;
import com.barberturn.application.port.in.AvailabilityUseCase;
import com.barberturn.application.port.in.BookAppointmentUseCase;
import com.barberturn.application.port.in.BusinessHoursUseCase;
import com.barberturn.application.port.in.ServiceCatalogUseCase;
import com.barberturn.application.port.out.AdminAccountPort;
import com.barberturn.application.port.out.AppointmentRepositoryPort;
import com.barberturn.application.port.out.BusinessHoursRepositoryPort;
import com.barberturn.application.port.out.PasswordHasherPort;
import com.barberturn.application.port.out.ServiceRepositoryPort;
import com.barberturn.application.port.out.TokenProviderPort;
import com.barberturn.application.usecase.AppointmentManagementService;
import com.barberturn.application.usecase.AuthService;
import com.barberturn.application.usecase.AvailabilityService;
import com.barberturn.application.usecase.BookAppointmentService;
import com.barberturn.application.usecase.BusinessHoursService;
import com.barberturn.application.usecase.ServiceCatalogService;
import com.barberturn.domain.service.AvailabilityCalculator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, CorsProperties.class, AdminProperties.class})
public class ApplicationConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public AvailabilityCalculator availabilityCalculator() {
        return new AvailabilityCalculator();
    }

    @Bean
    public ServiceCatalogUseCase serviceCatalogUseCase(ServiceRepositoryPort serviceRepository) {
        return new ServiceCatalogService(serviceRepository);
    }

    @Bean
    public AvailabilityUseCase availabilityUseCase(
            ServiceRepositoryPort serviceRepository,
            BusinessHoursRepositoryPort businessHoursRepository,
            AppointmentRepositoryPort appointmentRepository,
            AvailabilityCalculator availabilityCalculator,
            Clock clock
    ) {
        return new AvailabilityService(serviceRepository, businessHoursRepository,
                appointmentRepository, availabilityCalculator, clock);
    }

    @Bean
    public BookAppointmentUseCase bookAppointmentUseCase(
            ServiceRepositoryPort serviceRepository,
            BusinessHoursRepositoryPort businessHoursRepository,
            AppointmentRepositoryPort appointmentRepository,
            AvailabilityCalculator availabilityCalculator,
            Clock clock
    ) {
        return new BookAppointmentService(serviceRepository, businessHoursRepository,
                appointmentRepository, availabilityCalculator, clock);
    }

    @Bean
    public AppointmentManagementUseCase appointmentManagementUseCase(AppointmentRepositoryPort repository) {
        return new AppointmentManagementService(repository);
    }

    @Bean
    public BusinessHoursUseCase businessHoursUseCase(BusinessHoursRepositoryPort repository) {
        return new BusinessHoursService(repository);
    }

    @Bean
    public AuthUseCase authUseCase(
            AdminAccountPort adminAccountPort,
            PasswordHasherPort passwordHasher,
            TokenProviderPort tokenProvider
    ) {
        return new AuthService(adminAccountPort, passwordHasher, tokenProvider);
    }
}
