package com.barberturn.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookAppointmentRequest(
        @NotNull(message = "El servicio es obligatorio")
        Long serviceId,

        @NotNull(message = "La fecha es obligatoria")
        @FutureOrPresent(message = "No se pueden agendar citas en fechas pasadas")
        LocalDate date,

        @NotNull(message = "La hora es obligatoria")
        LocalTime startTime,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String customerName,

        @NotBlank(message = "El telefono es obligatorio")
        @Pattern(regexp = "[0-9+()\\-\\s]{7,30}", message = "El telefono tiene un formato invalido")
        String customerPhone,

        @Email(message = "El correo tiene un formato invalido")
        @Size(max = 150, message = "El correo no puede superar 150 caracteres")
        String customerEmail,

        @Size(max = 500, message = "Las notas no pueden superar 500 caracteres")
        String notes
) {
}
