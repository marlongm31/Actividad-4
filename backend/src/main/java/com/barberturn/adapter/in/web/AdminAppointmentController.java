package com.barberturn.adapter.in.web;

import com.barberturn.adapter.in.web.dto.AppointmentResponse;
import com.barberturn.adapter.in.web.dto.DashboardResponse;
import com.barberturn.adapter.in.web.dto.StatusUpdateRequest;
import com.barberturn.adapter.in.web.mapper.AppointmentWebMapper;
import com.barberturn.application.port.in.AppointmentManagementUseCase;
import com.barberturn.application.port.in.dto.DashboardStats;
import com.barberturn.domain.model.AppointmentStatus;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminAppointmentController {

    private final AppointmentManagementUseCase appointmentManagementUseCase;

    public AdminAppointmentController(AppointmentManagementUseCase appointmentManagementUseCase) {
        this.appointmentManagementUseCase = appointmentManagementUseCase;
    }

    @GetMapping("/appointments")
    public List<AppointmentResponse> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status
    ) {
        return appointmentManagementUseCase.searchAppointments(date, status).stream()
                .map(AppointmentWebMapper::toResponse)
                .toList();
    }

    @GetMapping("/appointments/{id}")
    public AppointmentResponse detail(@PathVariable Long id) {
        return AppointmentWebMapper.toResponse(appointmentManagementUseCase.findAppointment(id));
    }

    @PatchMapping("/appointments/{id}/status")
    public AppointmentResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        return AppointmentWebMapper.toResponse(
                appointmentManagementUseCase.changeStatus(id, request.status()));
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate target = date == null ? LocalDate.now() : date;
        DashboardStats stats = appointmentManagementUseCase.dashboard(target);
        return new DashboardResponse(target, stats.todayAppointments(), stats.pending(),
                stats.confirmed(), stats.completed(), stats.cancelled(), stats.estimatedRevenue());
    }
}
