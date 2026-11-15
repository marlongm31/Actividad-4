package com.barberturn.adapter.in.web;

import com.barberturn.adapter.in.web.dto.BusinessHoursResponse;
import com.barberturn.adapter.in.web.dto.ScheduleRequest;
import com.barberturn.adapter.in.web.mapper.BusinessHoursWebMapper;
import com.barberturn.application.port.in.BusinessHoursUseCase;
import com.barberturn.application.port.in.dto.BusinessHoursCommand;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/business-hours")
public class AdminScheduleController {

    private final BusinessHoursUseCase businessHoursUseCase;

    public AdminScheduleController(BusinessHoursUseCase businessHoursUseCase) {
        this.businessHoursUseCase = businessHoursUseCase;
    }

    @GetMapping
    public List<BusinessHoursResponse> list() {
        return businessHoursUseCase.getAllDays().stream()
                .map(BusinessHoursWebMapper::toResponse)
                .toList();
    }

    @PutMapping
    public List<BusinessHoursResponse> update(@Valid @RequestBody ScheduleRequest request) {
        List<BusinessHoursCommand> commands = request.days().stream()
                .map(day -> new BusinessHoursCommand(
                        day.dayOfWeek(), day.openTime(), day.closeTime(), day.active()))
                .toList();
        return businessHoursUseCase.updateSchedule(commands).stream()
                .map(BusinessHoursWebMapper::toResponse)
                .toList();
    }
}
