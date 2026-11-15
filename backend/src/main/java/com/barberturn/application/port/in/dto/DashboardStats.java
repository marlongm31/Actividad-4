package com.barberturn.application.port.in.dto;

import java.math.BigDecimal;

public record DashboardStats(
        long todayAppointments,
        long pending,
        long confirmed,
        long completed,
        long cancelled,
        BigDecimal estimatedRevenue
) {
}
