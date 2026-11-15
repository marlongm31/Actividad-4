package com.barberturn.domain;

import com.barberturn.domain.exception.DomainException;
import com.barberturn.domain.model.Appointment;
import com.barberturn.domain.model.AppointmentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AppointmentStatusTest {

    @Test
    void allowsValidTransitions() {
        assertThat(AppointmentStatus.PENDING.canTransitionTo(AppointmentStatus.CONFIRMED)).isTrue();
        assertThat(AppointmentStatus.PENDING.canTransitionTo(AppointmentStatus.CANCELLED)).isTrue();
        assertThat(AppointmentStatus.CONFIRMED.canTransitionTo(AppointmentStatus.COMPLETED)).isTrue();
        assertThat(AppointmentStatus.CONFIRMED.canTransitionTo(AppointmentStatus.CANCELLED)).isTrue();
    }

    @Test
    void blocksInvalidTransitions() {
        assertThat(AppointmentStatus.COMPLETED.canTransitionTo(AppointmentStatus.CONFIRMED)).isFalse();
        assertThat(AppointmentStatus.CANCELLED.canTransitionTo(AppointmentStatus.CONFIRMED)).isFalse();
        assertThat(AppointmentStatus.PENDING.canTransitionTo(AppointmentStatus.COMPLETED)).isFalse();
        assertThat(AppointmentStatus.PENDING.canTransitionTo(AppointmentStatus.PENDING)).isFalse();
    }

    @Test
    void marksFinalStates() {
        assertThat(AppointmentStatus.COMPLETED.isFinal()).isTrue();
        assertThat(AppointmentStatus.CANCELLED.isFinal()).isTrue();
        assertThat(AppointmentStatus.PENDING.isFinal()).isFalse();
        assertThat(AppointmentStatus.CONFIRMED.isFinal()).isFalse();
    }

    @Test
    void appointmentRejectsStatusChangeThatBreaksTheWorkflow() {
        Appointment completed = appointment(AppointmentStatus.COMPLETED);

        assertThatThrownBy(() -> completed.withStatus(AppointmentStatus.CONFIRMED))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("No se puede cambiar una cita");
    }

    @Test
    void appointmentUpdatesStatusWhenTheTransitionIsValid() {
        Appointment pending = appointment(AppointmentStatus.PENDING);

        Appointment confirmed = pending.withStatus(AppointmentStatus.CONFIRMED);

        assertThat(confirmed.status()).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(confirmed.id()).isEqualTo(pending.id());
    }

    private Appointment appointment(AppointmentStatus status) {
        return new Appointment(
                1L, 1L, "Corte clasico", new BigDecimal("25000"), 45,
                "Cliente", "3001234567", null, null,
                LocalDate.of(2026, 11, 16), LocalTime.of(10, 0), LocalTime.of(10, 45),
                status, LocalDateTime.of(2026, 11, 15, 9, 0));
    }
}
