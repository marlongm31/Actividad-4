package com.barberturn.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServiceRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String name,

        @Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
        String description,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
        @Digits(integer = 8, fraction = 2, message = "El precio tiene un formato invalido")
        BigDecimal price,

        @Min(value = 15, message = "La duracion minima es de 15 minutos")
        @Max(value = 240, message = "La duracion maxima es de 240 minutos")
        int durationMinutes,

        Boolean active
) {
}
