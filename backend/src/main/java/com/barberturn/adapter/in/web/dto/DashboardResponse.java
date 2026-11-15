package com.barberturn.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardResponse(
        LocalDate date,
        long todayAppointments,
        long pending,
        long confirmed,
        long completed,
        long cancelled,
        BigDecimal estimatedRevenue
) {
}
