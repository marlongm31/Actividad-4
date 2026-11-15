package com.barberturn.application.port.in;

import com.barberturn.application.port.in.dto.BusinessHoursCommand;
import com.barberturn.domain.model.BusinessHours;

import java.util.List;

public interface BusinessHoursUseCase {

    List<BusinessHours> getAllDays();

    List<BusinessHours> updateSchedule(List<BusinessHoursCommand> commands);
}
