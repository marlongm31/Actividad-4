package com.barberturn.adapter.out.persistence.repository;

import com.barberturn.adapter.out.persistence.entity.AdminUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserJpaRepository extends JpaRepository<AdminUserEntity, Long> {

    Optional<AdminUserEntity> findByEmail(String email);
}
