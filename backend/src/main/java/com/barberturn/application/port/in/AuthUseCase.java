package com.barberturn.application.port.in;

import com.barberturn.application.port.in.dto.AuthResult;
import com.barberturn.application.port.in.dto.LoginCommand;

public interface AuthUseCase {

    AuthResult login(LoginCommand command);
}
