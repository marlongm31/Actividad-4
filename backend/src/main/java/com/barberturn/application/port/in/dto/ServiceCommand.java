package com.barberturn.application.port.in.dto;

import java.math.BigDecimal;

public record ServiceCommand(
        String name,
        String description,
        BigDecimal price,
        int durationMinutes,
        Boolean active
) {
}
