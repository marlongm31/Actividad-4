package com.barberturn.adapter.in.web.dto;

import com.barberturn.domain.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull(message = "El estado es obligatorio")
        AppointmentStatus status
) {
}
