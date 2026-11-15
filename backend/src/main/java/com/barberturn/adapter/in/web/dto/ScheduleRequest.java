package com.barberturn.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.List;

public record ScheduleRequest(
        @NotEmpty(message = "Debe enviar al menos un dia")
        @Size(max = 7, message = "Solo existen 7 dias en la semana")
        List<@Valid DaySchedule> days
) {

    public record DaySchedule(
            @Min(value = 1, message = "El dia debe estar entre 1 y 7")
            @Max(value = 7, message = "El dia debe estar entre 1 y 7")
            int dayOfWeek,

            @NotNull(message = "La hora de apertura es obligatoria")
            LocalTime openTime,

            @NotNull(message = "La hora de cierre es obligatoria")
            LocalTime closeTime,

            boolean active
    ) {
    }
}
