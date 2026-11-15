package com.barberturn.application.usecase;

import com.barberturn.application.port.in.BusinessHoursUseCase;
import com.barberturn.application.port.in.dto.BusinessHoursCommand;
import com.barberturn.application.port.out.BusinessHoursRepositoryPort;
import com.barberturn.domain.model.BusinessHours;

import java.util.Comparator;
import java.util.List;

public class BusinessHoursService implements BusinessHoursUseCase {

    private final BusinessHoursRepositoryPort businessHoursRepository;

    public BusinessHoursService(BusinessHoursRepositoryPort businessHoursRepository) {
        this.businessHoursRepository = businessHoursRepository;
    }

    @Override
    public List<BusinessHours> getAllDays() {
        return businessHoursRepository.findAll().stream()
                .sorted(Comparator.comparingInt(BusinessHours::dayOfWeek))
                .toList();
    }

    @Override
    public List<BusinessHours> updateSchedule(List<BusinessHoursCommand> commands) {
        commands.forEach(this::updateDay);
        return getAllDays();
    }

    private void updateDay(BusinessHoursCommand command) {
        BusinessHours existing = businessHoursRepository.findByDayOfWeek(command.dayOfWeek())
                .orElse(new BusinessHours(null, command.dayOfWeek(),
                        command.openTime(), command.closeTime(), command.active()));
        BusinessHours updated = existing.update(command.openTime(), command.closeTime(), command.active());
        businessHoursRepository.save(updated);
    }
}
