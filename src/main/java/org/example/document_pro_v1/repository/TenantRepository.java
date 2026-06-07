package org.example.document_pro_v1.repository;

import org.example.document_pro_v1.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant,Long> {
    Optional<Tenant> findBySlug(String slug);
}
