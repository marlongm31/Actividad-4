package com.barberturn.application.port.out;

import com.barberturn.domain.model.AdminUser;

import java.util.Optional;

public interface AdminAccountPort {

    Optional<AdminUser> findByEmail(String email);

    AdminUser save(AdminUser adminUser);
}
