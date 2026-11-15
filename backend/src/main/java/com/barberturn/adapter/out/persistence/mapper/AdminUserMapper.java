package com.barberturn.adapter.out.persistence.mapper;

import com.barberturn.adapter.out.persistence.entity.AdminUserEntity;
import com.barberturn.domain.model.AdminUser;

public final class AdminUserMapper {

    private AdminUserMapper() {
    }

    public static AdminUser toDomain(AdminUserEntity entity) {
        return new AdminUser(entity.getId(), entity.getEmail(), entity.getPasswordHash(), entity.getName());
    }

    public static AdminUserEntity toEntity(AdminUser adminUser) {
        return new AdminUserEntity(adminUser.id(), adminUser.email(),
                adminUser.passwordHash(), adminUser.name());
    }
}
