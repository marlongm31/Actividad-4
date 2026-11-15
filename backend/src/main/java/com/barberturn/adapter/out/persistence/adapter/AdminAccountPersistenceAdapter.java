package com.barberturn.adapter.out.persistence.adapter;

import com.barberturn.adapter.out.persistence.mapper.AdminUserMapper;
import com.barberturn.adapter.out.persistence.repository.AdminUserJpaRepository;
import com.barberturn.application.port.out.AdminAccountPort;
import com.barberturn.domain.model.AdminUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AdminAccountPersistenceAdapter implements AdminAccountPort {

    private final AdminUserJpaRepository repository;

    public AdminAccountPersistenceAdapter(AdminUserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<AdminUser> findByEmail(String email) {
        return repository.findByEmail(email).map(AdminUserMapper::toDomain);
    }

    @Override
    public AdminUser save(AdminUser adminUser) {
        return AdminUserMapper.toDomain(repository.save(AdminUserMapper.toEntity(adminUser)));
    }
}
