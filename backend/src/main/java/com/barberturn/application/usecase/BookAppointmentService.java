package com.barberturn.application.usecase;

import com.barberturn.application.port.in.BookAppointmentUseCase;
import com.barberturn.application.port.in.dto.BookAppointmentCommand;
import com.barberturn.application.port.out.AppointmentRepositoryPort;
import com.barberturn.application.port.out.BusinessHoursRepositoryPort;
import com.barberturn.application.port.out.ServiceRepositoryPort;
import com.barberturn.domain.exception.NotFoundException;
import com.barberturn.domain.model.Appointment;
import com.barberturn.domain.model.BusinessHours;
import com.barberturn.domain.model.Service;
import com.barberturn.domain.service.AvailabilityCalculator;

import java.time.Clock;
import java.time.LocalDateTime;

public class BookAppointmentService implements BookAppointmentUseCase {

    private final ServiceRepositoryPort serviceRepository;
    private final BusinessHoursRepositoryPort businessHoursRepository;
    private final AppointmentRepositoryPort appointmentRepository;
    private final AvailabilityCalculator availabilityCalculator;
    private final Clock clock;

    public BookAppointmentService(
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
    public Appointment bookAppointment(BookAppointmentCommand command) {
        Service service = serviceRepository.findById(command.serviceId())
                .filter(Service::active)
                .orElseThrow(() -> new NotFoundException("El servicio solicitado no esta disponible"));

        BusinessHours hours = businessHoursRepository
                .findByDayOfWeek(command.date().getDayOfWeek().getValue())
                .orElse(null);

        LocalDateTime now = LocalDateTime.now(clock);
        var dayAppointments = appointmentRepository.findByDate(command.date());

        availabilityCalculator.validateBooking(
                command.date(), command.startTime(), service, hours, dayAppointments, now);

        Appointment appointment = Appointment.create(
                service,
                command.customerName(),
                command.customerPhone(),
                command.customerEmail(),
                command.notes(),
                command.date(),
                command.startTime(),
                now);

        return appointmentRepository.save(appointment);
    }
}
