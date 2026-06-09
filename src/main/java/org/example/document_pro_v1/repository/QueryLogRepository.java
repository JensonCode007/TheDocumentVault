package org.example.document_pro_v1.repository;

import org.example.document_pro_v1.entity.QueryLog;
import org.example.document_pro_v1.entity.Tenant;
import org.example.document_pro_v1.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryLogRepository extends JpaRepository<QueryLog, Long> {
    Page<QueryLog> findByUser(User user,  Pageable pageable);

    Page<QueryLog> findByTenant(Tenant tenant, Pageable pageable);

}
