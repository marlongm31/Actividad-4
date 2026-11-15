package com.barberturn.adapter.in.web.mapper;

import com.barberturn.adapter.in.web.dto.BusinessHoursResponse;
import com.barberturn.domain.model.BusinessHours;

import java.util.List;

public final class BusinessHoursWebMapper {

    private static final List<String> DAY_NAMES = List.of(
            "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo");

    private BusinessHoursWebMapper() {
    }

    public static BusinessHoursResponse toResponse(BusinessHours hours) {
        return new BusinessHoursResponse(
                hours.dayOfWeek(),
                dayName(hours.dayOfWeek()),
                hours.openTime(),
                hours.closeTime(),
                hours.active());
    }

    public static String dayName(int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            return "Desconocido";
        }
        return DAY_NAMES.get(dayOfWeek - 1);
    }
}
