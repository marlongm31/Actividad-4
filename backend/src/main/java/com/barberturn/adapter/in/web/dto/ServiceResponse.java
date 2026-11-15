package com.barberturn.adapter.in.web.dto;

import java.math.BigDecimal;

public record ServiceResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int durationMinutes,
        boolean active
) {
}
