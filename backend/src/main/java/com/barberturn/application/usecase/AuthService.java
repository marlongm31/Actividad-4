package com.barberturn.application.usecase;

import com.barberturn.application.port.in.AuthUseCase;
import com.barberturn.application.port.in.dto.AuthResult;
import com.barberturn.application.port.in.dto.LoginCommand;
import com.barberturn.application.port.out.AdminAccountPort;
import com.barberturn.application.port.out.PasswordHasherPort;
import com.barberturn.application.port.out.TokenProviderPort;
import com.barberturn.domain.exception.InvalidCredentialsException;
import com.barberturn.domain.model.AdminUser;

import java.util.Locale;

public class AuthService implements AuthUseCase {

    private final AdminAccountPort adminAccountPort;
    private final PasswordHasherPort passwordHasher;
    private final TokenProviderPort tokenProvider;

    public AuthService(
            AdminAccountPort adminAccountPort,
            PasswordHasherPort passwordHasher,
            TokenProviderPort tokenProvider
    ) {
        this.adminAccountPort = adminAccountPort;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthResult login(LoginCommand command) {
        if (command.email() == null || command.password() == null) {
            throw new InvalidCredentialsException("Credenciales invalidas");
        }
        String email = command.email().trim().toLowerCase(Locale.ROOT);
        AdminUser admin = adminAccountPort.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Correo o contrasena incorrectos"));

        if (!passwordHasher.matches(command.password(), admin.passwordHash())) {
            throw new InvalidCredentialsException("Correo o contrasena incorrectos");
        }

        String token = tokenProvider.createToken(admin.email());
        return new AuthResult(token, admin.name(), admin.email(), tokenProvider.expirationSeconds());
    }
}
