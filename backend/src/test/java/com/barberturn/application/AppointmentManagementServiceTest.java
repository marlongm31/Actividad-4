package com.barberturn.application;

import com.barberturn.application.port.out.AppointmentRepositoryPort;
import com.barberturn.application.usecase.AppointmentManagementService;
import com.barberturn.domain.exception.DomainException;
import com.barberturn.domain.exception.NotFoundException;
import com.barberturn.domain.model.Appointment;
import com.barberturn.domain.model.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentManagementServiceTest {

    private static final LocalDate DATE = LocalDate.of(2026, 11, 16);

    @Mock
    private AppointmentRepositoryPort repository;

    private AppointmentManagementService service;

    @BeforeEach
    void setUp() {
        service = new AppointmentManagementService(repository);
    }

    @Test
    void changesTheStatusWhenTheTransitionIsValid() {
        Appointment pending = appointment(1L, "10:00", AppointmentStatus.PENDING, "25000");
        when(repository.findById(1L)).thenReturn(Optional.of(pending));
        when(repository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appointment confirmed = service.changeStatus(1L, AppointmentStatus.CONFIRMED);

        assertThat(confirmed.status()).isEqualTo(AppointmentStatus.CONFIRMED);
    }

    @Test
    void rejectsChangingACompletedAppointment() {
        Appointment completed = appointment(1L, "10:00", AppointmentStatus.COMPLETED, "25000");
        when(repository.findById(1L)).thenReturn(Optional.of(completed));

        assertThatThrownBy(() -> service.changeStatus(1L, AppointmentStatus.CANCELLED))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("No se puede cambiar");
    }

    @Test
    void throwsWhenTheAppointmentDoesNotExist() {
        when(repository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findAppointment(10L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void buildsDashboardStatisticsExcludingCancelledRevenue() {
        when(repository.findByDate(DATE)).thenReturn(List.of(
                appointment(1L, "09:00", AppointmentStatus.PENDING, "25000"),
                appointment(2L, "10:00", AppointmentStatus.CONFIRMED, "38000"),
                appointment(3L, "11:00", AppointmentStatus.COMPLETED, "18000"),
                appointment(4L, "12:00", AppointmentStatus.CANCELLED, "22000")));

        var stats = service.dashboard(DATE);

        assertThat(stats.todayAppointments()).isEqualTo(4);
        assertThat(stats.pending()).isEqualTo(1);
        assertThat(stats.confirmed()).isEqualTo(1);
        assertThat(stats.completed()).isEqualTo(1);
        assertThat(stats.cancelled()).isEqualTo(1);
        assertThat(stats.estimatedRevenue()).isEqualByComparingTo("81000");
    }

    @Test
    void filtersByStatusWhenNoDateIsProvided() {
        when(repository.findAll()).thenReturn(List.of(
                appointment(1L, "09:00", AppointmentStatus.PENDING, "25000"),
                appointment(2L, "10:00", AppointmentStatus.CONFIRMED, "38000")));

        var result = service.searchAppointments(null, AppointmentStatus.CONFIRMED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(AppointmentStatus.CONFIRMED);
    }

    private Appointment appointment(Long id, String start, AppointmentStatus status, String price) {
        LocalTime startTime = LocalTime.parse(start);
        return new Appointment(
                id, 1L, "Corte clasico", new BigDecimal(price), 60,
                "Cliente " + id, "3001234567", null, null,
                DATE, startTime, startTime.plusMinutes(60), status,
                LocalDateTime.of(2026, 11, 15, 9, 0));
    }
}
