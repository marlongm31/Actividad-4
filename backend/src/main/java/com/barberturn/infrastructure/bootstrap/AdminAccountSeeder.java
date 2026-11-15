package com.barberturn.infrastructure.bootstrap;

import com.barberturn.application.port.out.AdminAccountPort;
import com.barberturn.application.port.out.PasswordHasherPort;
import com.barberturn.domain.model.AdminUser;
import com.barberturn.infrastructure.config.AdminProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AdminAccountSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminAccountSeeder.class);

    private final AdminAccountPort adminAccountPort;
    private final PasswordHasherPort passwordHasher;
    private final AdminProperties adminProperties;

    public AdminAccountSeeder(
            AdminAccountPort adminAccountPort,
            PasswordHasherPort passwordHasher,
            AdminProperties adminProperties
    ) {
        this.adminAccountPort = adminAccountPort;
        this.passwordHasher = passwordHasher;
        this.adminProperties = adminProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        String email = adminProperties.email().trim().toLowerCase(Locale.ROOT);
        if (adminAccountPort.findByEmail(email).isPresent()) {
            log.info("Cuenta administrativa ya existente para {}", email);
            return;
        }
        AdminUser admin = new AdminUser(null, email,
                passwordHasher.hash(adminProperties.password()), adminProperties.name());
        adminAccountPort.save(admin);
        log.info("Cuenta administrativa creada para {}", email);
    }
}
