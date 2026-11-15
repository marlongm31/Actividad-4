package com.barberturn.domain;

import com.barberturn.domain.exception.BusinessRuleException;
import com.barberturn.domain.exception.ConflictException;
import com.barberturn.domain.model.Appointment;
import com.barberturn.domain.model.AppointmentStatus;
import com.barberturn.domain.model.BusinessHours;
import com.barberturn.domain.model.Service;
import com.barberturn.domain.model.TimeSlot;
import com.barberturn.domain.service.AvailabilityCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AvailabilityCalculatorTest {

    private static final LocalDate DATE = LocalDate.of(2026, 11, 16);
    private final AvailabilityCalculator calculator = new AvailabilityCalculator();
    private final Service service = new Service(
            1L, "Corte clasico", "Corte", new BigDecimal("25000"), 60, true);
    private final BusinessHours hours = new BusinessHours(
            1L, 1, LocalTime.of(9, 0), LocalTime.of(19, 0), true);

    @Test
    void generatesSlotsEveryFifteenMinutesWithinBusinessHours() {
        var slots = calculator.availableSlots(
                DATE, hours, service, List.of(), LocalDateTime.of(2026, 11, 16, 8, 0));

        assertThat(slots).hasSize(37);
        assertThat(slots).extracting(TimeSlot::start)
                .first().isEqualTo(LocalTime.of(9, 0));
        assertThat(slots).extracting(TimeSlot::start)
                .last().isEqualTo(LocalTime.of(18, 0));
    }

    @Test
    void removesSlotsThatOverlapWithExistingAppointments() {
        Appointment existing = appointment("10:00", "11:00", AppointmentStatus.CONFIRMED);

        var slots = calculator.availableSlots(
                DATE, hours, service, List.of(existing), LocalDateTime.of(2026, 11, 16, 8, 0));

        assertThat(slots).hasSize(30);
        assertThat(slots).extracting(TimeSlot::start)
                .contains(LocalTime.of(9, 0), LocalTime.of(11, 0))
                .doesNotContain(LocalTime.of(9, 15), LocalTime.of(10, 0), LocalTime.of(10, 45));
    }

    @Test
    void ignoresCancelledAppointmentsWhenCalculatingAvailability() {
        Appointment cancelled = appointment("10:00", "11:00", AppointmentStatus.CANCELLED);

        var slots = calculator.availableSlots(
                DATE, hours, service, List.of(cancelled), LocalDateTime.of(2026, 11, 16, 8, 0));

        assertThat(slots).hasSize(37);
        assertThat(slots).extracting(TimeSlot::start).contains(LocalTime.of(10, 0));
    }

    @Test
    void filtersOutPastSlotsForToday() {
        var slots = calculator.availableSlots(
                DATE, hours, service, List.of(), LocalDateTime.of(2026, 11, 16, 10, 0));

        assertThat(slots).extracting(TimeSlot::start)
                .doesNotContain(LocalTime.of(9, 0), LocalTime.of(10, 0))
                .startsWith(LocalTime.of(10, 15));
    }

    @Test
    void returnsNoSlotsWhenTheDayIsClosed() {
        BusinessHours closed = new BusinessHours(
                7L, 7, LocalTime.of(9, 0), LocalTime.of(13, 0), false);

        var slots = calculator.availableSlots(
                DATE, closed, service, List.of(), LocalDateTime.of(2026, 11, 16, 8, 0));

        assertThat(slots).isEmpty();
    }

    @Test
    void rejectsBookingOutsideBusinessHours() {
        assertThatThrownBy(() -> calculator.validateBooking(
                DATE, LocalTime.of(18, 30), service, hours, List.of(),
                LocalDateTime.of(2026, 11, 16, 8, 0)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("fuera del horario");
    }

    @Test
    void rejectsBookingWhenTheSlotIsAlreadyTaken() {
        Appointment existing = appointment("10:00", "11:00", AppointmentStatus.PENDING);

        assertThatThrownBy(() -> calculator.validateBooking(
                DATE, LocalTime.of(10, 30), service, hours, List.of(existing),
                LocalDateTime.of(2026, 11, 16, 8, 0)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ya fue reservado");
    }

    @Test
    void rejectsBookingInThePast() {
        assertThatThrownBy(() -> calculator.validateBooking(
                DATE, LocalTime.of(12, 0), service, hours, List.of(),
                LocalDateTime.of(2026, 11, 16, 13, 0)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("pasadas");
    }

    private Appointment appointment(String start, String end, AppointmentStatus status) {
        return new Appointment(
                10L, 1L, "Corte clasico", new BigDecimal("25000"), 60,
                "Cliente", "3001234567", null, null,
                DATE, LocalTime.parse(start), LocalTime.parse(end), status,
                LocalDateTime.of(2026, 11, 15, 9, 0));
    }
}
