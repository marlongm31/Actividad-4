package com.barberturn.application.port.in;

import com.barberturn.application.port.in.dto.BookAppointmentCommand;
import com.barberturn.domain.model.Appointment;

public interface BookAppointmentUseCase {

    Appointment bookAppointment(BookAppointmentCommand command);
}
