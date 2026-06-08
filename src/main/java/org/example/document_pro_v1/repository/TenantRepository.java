package org.example.document_pro_v1.repository;

import org.example.document_pro_v1.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant,Long> {
    Optional<Tenant> findBySlug(String slug);
}
