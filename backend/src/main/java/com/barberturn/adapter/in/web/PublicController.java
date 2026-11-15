package com.barberturn.adapter.in.web;

import com.barberturn.adapter.in.web.dto.AppointmentResponse;
import com.barberturn.adapter.in.web.dto.AvailabilityResponse;
import com.barberturn.adapter.in.web.dto.BookAppointmentRequest;
import com.barberturn.adapter.in.web.dto.ServiceResponse;
import com.barberturn.adapter.in.web.mapper.AppointmentWebMapper;
import com.barberturn.adapter.in.web.mapper.ServiceWebMapper;
import com.barberturn.application.port.in.AvailabilityUseCase;
import com.barberturn.application.port.in.BookAppointmentUseCase;
import com.barberturn.application.port.in.ServiceCatalogUseCase;
import com.barberturn.application.port.in.dto.BookAppointmentCommand;
import com.barberturn.domain.model.Appointment;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final ServiceCatalogUseCase serviceCatalogUseCase;
    private final AvailabilityUseCase availabilityUseCase;
    private final BookAppointmentUseCase bookAppointmentUseCase;

    public PublicController(
            ServiceCatalogUseCase serviceCatalogUseCase,
            AvailabilityUseCase availabilityUseCase,
            BookAppointmentUseCase bookAppointmentUseCase
    ) {
        this.serviceCatalogUseCase = serviceCatalogUseCase;
        this.availabilityUseCase = availabilityUseCase;
        this.bookAppointmentUseCase = bookAppointmentUseCase;
    }

    @GetMapping("/services")
    public List<ServiceResponse> listServices() {
        return serviceCatalogUseCase.listActiveServices().stream()
                .map(ServiceWebMapper::toResponse)
                .toList();
    }

    @GetMapping("/availability")
    public AvailabilityResponse availability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long serviceId
    ) {
        var slots = availabilityUseCase.availableSlots(date, serviceId).stream()
                .map(slot -> new AvailabilityResponse.Slot(slot.start(), slot.end()))
                .toList();
        return new AvailabilityResponse(date, serviceId, slots);
    }

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody BookAppointmentRequest request) {
        Appointment appointment = bookAppointmentUseCase.bookAppointment(new BookAppointmentCommand(
                request.serviceId(),
                request.date(),
                request.startTime(),
                request.customerName(),
                request.customerPhone(),
                normalize(request.customerEmail()),
                normalize(request.notes())));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppointmentWebMapper.toResponse(appointment));
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
