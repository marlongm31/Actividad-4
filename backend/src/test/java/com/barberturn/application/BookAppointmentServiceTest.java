package com.barberturn.application;

import com.barberturn.application.port.in.dto.BookAppointmentCommand;
import com.barberturn.application.port.out.AppointmentRepositoryPort;
import com.barberturn.application.port.out.BusinessHoursRepositoryPort;
import com.barberturn.application.port.out.ServiceRepositoryPort;
import com.barberturn.application.usecase.BookAppointmentService;
import com.barberturn.domain.exception.BusinessRuleException;
import com.barberturn.domain.exception.ConflictException;
import com.barberturn.domain.exception.NotFoundException;
import com.barberturn.domain.model.Appointment;
import com.barberturn.domain.model.AppointmentStatus;
import com.barberturn.domain.model.BusinessHours;
import com.barberturn.domain.model.Service;
import com.barberturn.domain.service.AvailabilityCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookAppointmentServiceTest {

    private static final LocalDate DATE = LocalDate.of(2026, 11, 16);

    @Mock
    private ServiceRepositoryPort serviceRepository;
    @Mock
    private BusinessHoursRepositoryPort businessHoursRepository;
    @Mock
    private AppointmentRepositoryPort appointmentRepository;

    private BookAppointmentService useCase;
    private Service activeService;
    private BusinessHours openHours;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(
                LocalDateTime.of(2026, 11, 16, 8, 0).toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        useCase = new BookAppointmentService(
                serviceRepository, businessHoursRepository, appointmentRepository,
                new AvailabilityCalculator(), clock);
        activeService = new Service(1L, "Corte clasico", "Corte", new BigDecimal("25000"), 60, true);
        openHours = new BusinessHours(1L, 1, LocalTime.of(9, 0), LocalTime.of(19, 0), true);
    }

    @Test
    void booksAnAppointmentWhenTheSlotIsAvailable() {
        stubAvailableDay();
        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.<Appointment>getArgument(0).withId(99L));

        Appointment appointment = useCase.bookAppointment(command("10:00"));

        assertThat(appointment.id()).isEqualTo(99L);
        assertThat(appointment.status()).isEqualTo(AppointmentStatus.PENDING);
        assertThat(appointment.serviceName()).isEqualTo("Corte clasico");
        assertThat(appointment.endTime()).isEqualTo(LocalTime.of(11, 0));
        assertThat(appointment.customerName()).isEqualTo("Juan Perez");
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void rejectsAnOverlappingAppointment() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(activeService));
        when(businessHoursRepository.findByDayOfWeek(1)).thenReturn(Optional.of(openHours));
        when(appointmentRepository.findByDate(DATE)).thenReturn(List.of(
                appointmentAt("10:00", "11:00", AppointmentStatus.CONFIRMED)));

        assertThatThrownBy(() -> useCase.bookAppointment(command("10:30")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ya fue reservado");
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void rejectsAnAppointmentInThePast() {
        stubAvailableDay();

        assertThatThrownBy(() -> useCase.bookAppointment(command("07:00")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("pasadas");
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void rejectsAnAppointmentOutsideBusinessHours() {
        stubAvailableDay();

        assertThatThrownBy(() -> useCase.bookAppointment(command("20:00")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("fuera del horario");
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void rejectsWhenTheBusinessIsClosedThatDay() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(activeService));
        BusinessHours closed = new BusinessHours(
                7L, 1, LocalTime.of(9, 0), LocalTime.of(19, 0), false);
        when(businessHoursRepository.findByDayOfWeek(1)).thenReturn(Optional.of(closed));
        when(appointmentRepository.findByDate(DATE)).thenReturn(List.of());

        assertThatThrownBy(() -> useCase.bookAppointment(command("10:00")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("no atiende");
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void rejectsAnInactiveService() {
        Service inactive = new Service(1L, "Corte clasico", "Corte", new BigDecimal("25000"), 60, false);
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> useCase.bookAppointment(command("10:00")))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("no esta disponible");
    }

    private void stubAvailableDay() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(activeService));
        when(businessHoursRepository.findByDayOfWeek(1)).thenReturn(Optional.of(openHours));
        when(appointmentRepository.findByDate(DATE)).thenReturn(List.of());
    }

    private BookAppointmentCommand command(String startTime) {
        return new BookAppointmentCommand(
                1L, DATE, LocalTime.parse(startTime), "Juan Perez", "3001234567",
                "juan@example.com", "Primera visita");
    }

    private Appointment appointmentAt(String start, String end, AppointmentStatus status) {
        return new Appointment(
                5L, 1L, "Corte clasico", new BigDecimal("25000"), 60,
                "Otro Cliente", "3009999999", null, null,
                DATE, LocalTime.parse(start), LocalTime.parse(end), status,
                LocalDateTime.of(2026, 11, 15, 9, 0));
    }
}
