package org.example.document_pro_v1.repository;

import org.example.document_pro_v1.entity.QueryLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QueryLogRepository extends JpaRepository<QueryLog, String> {
    List<QueryLog> findAllByTenantIdOrderByQueriedAtDesc(String tenantId);
}
