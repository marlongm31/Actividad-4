package com.barberturn.adapter.in.web;

import com.barberturn.adapter.in.web.dto.LoginRequest;
import com.barberturn.adapter.in.web.dto.LoginResponse;
import com.barberturn.application.port.in.AuthUseCase;
import com.barberturn.application.port.in.dto.AuthResult;
import com.barberturn.application.port.in.dto.LoginCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = authUseCase.login(new LoginCommand(request.email(), request.password()));
        return ResponseEntity.ok(new LoginResponse(
                result.token(), result.name(), result.email(), result.expiresInSeconds()));
    }
}
