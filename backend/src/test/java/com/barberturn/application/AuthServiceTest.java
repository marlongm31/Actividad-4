package com.barberturn.application;

import com.barberturn.application.port.in.dto.LoginCommand;
import com.barberturn.application.port.out.AdminAccountPort;
import com.barberturn.application.port.out.PasswordHasherPort;
import com.barberturn.application.port.out.TokenProviderPort;
import com.barberturn.application.usecase.AuthService;
import com.barberturn.domain.exception.InvalidCredentialsException;
import com.barberturn.domain.model.AdminUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AdminAccountPort adminAccountPort;
    @Mock
    private PasswordHasherPort passwordHasher;
    @Mock
    private TokenProviderPort tokenProvider;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(adminAccountPort, passwordHasher, tokenProvider);
    }

    @Test
    void returnsATokenWhenTheCredentialsAreValid() {
        AdminUser admin = new AdminUser(1L, "admin@barberturn.com", "hash", "Martin");
        when(adminAccountPort.findByEmail("admin@barberturn.com")).thenReturn(Optional.of(admin));
        when(passwordHasher.matches("secreta", "hash")).thenReturn(true);
        when(tokenProvider.createToken("admin@barberturn.com")).thenReturn("jwt-token");
        when(tokenProvider.expirationSeconds()).thenReturn(28800L);

        var result = service.login(new LoginCommand("  ADMIN@barberturn.com ", "secreta"));

        assertThat(result.token()).isEqualTo("jwt-token");
        assertThat(result.name()).isEqualTo("Martin");
        assertThat(result.expiresInSeconds()).isEqualTo(28800L);
    }

    @Test
    void rejectsAnUnknownEmail() {
        when(adminAccountPort.findByEmail("nadie@barberturn.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginCommand("nadie@barberturn.com", "x")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void rejectsAnIncorrectPassword() {
        AdminUser admin = new AdminUser(1L, "admin@barberturn.com", "hash", "Martin");
        when(adminAccountPort.findByEmail("admin@barberturn.com")).thenReturn(Optional.of(admin));
        when(passwordHasher.matches("incorrecta", "hash")).thenReturn(false);

        assertThatThrownBy(() -> service.login(new LoginCommand("admin@barberturn.com", "incorrecta")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
