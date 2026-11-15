package com.barberturn.application.usecase;

import com.barberturn.application.port.in.AvailabilityUseCase;
import com.barberturn.application.port.out.AppointmentRepositoryPort;
import com.barberturn.application.port.out.BusinessHoursRepositoryPort;
import com.barberturn.application.port.out.ServiceRepositoryPort;
import com.barberturn.domain.exception.BusinessRuleException;
import com.barberturn.domain.exception.NotFoundException;
import com.barberturn.domain.model.Appointment;
import com.barberturn.domain.model.BusinessHours;
import com.barberturn.domain.model.Service;
import com.barberturn.domain.model.TimeSlot;
import com.barberturn.domain.service.AvailabilityCalculator;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AvailabilityService implements AvailabilityUseCase {

    private final ServiceRepositoryPort serviceRepository;
    private final BusinessHoursRepositoryPort businessHoursRepository;
    private final AppointmentRepositoryPort appointmentRepository;
    private final AvailabilityCalculator availabilityCalculator;
    private final Clock clock;

    public AvailabilityService(
            ServiceRepositoryPort serviceRepository,
            BusinessHoursRepositoryPort businessHoursRepository,
            AppointmentRepositoryPort appointmentRepository,
            AvailabilityCalculator availabilityCalculator,
            Clock clock
    ) {
        this.serviceRepository = serviceRepository;
        this.businessHoursRepository = businessHoursRepository;
        this.appointmentRepository = appointmentRepository;
        this.availabilityCalculator = availabilityCalculator;
        this.clock = clock;
    }

    @Override
    public List<TimeSlot> availableSlots(LocalDate date, Long serviceId) {
        if (date == null || serviceId == null) {
            throw new BusinessRuleException("La fecha y el servicio son obligatorios");
        }
        Service service = serviceRepository.findById(serviceId)
                .filter(Service::active)
                .orElseThrow(() -> new NotFoundException("El servicio solicitado no esta disponible"));

        BusinessHours hours = businessHoursRepository.findByDayOfWeek(date.getDayOfWeek().getValue())
                .orElse(null);
        List<Appointment> appointments = appointmentRepository.findByDate(date);

        LocalDateTime now = LocalDateTime.now(clock);
        return availabilityCalculator.availableSlots(date, hours, service, appointments, now);
    }
}
