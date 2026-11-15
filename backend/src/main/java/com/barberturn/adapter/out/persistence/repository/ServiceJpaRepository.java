package com.barberturn.adapter.out.persistence.repository;

import com.barberturn.adapter.out.persistence.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceJpaRepository extends JpaRepository<ServiceEntity, Long> {

    List<ServiceEntity> findByActiveTrueOrderByNameAsc();

    List<ServiceEntity> findAllByOrderByActiveDescNameAsc();
}
