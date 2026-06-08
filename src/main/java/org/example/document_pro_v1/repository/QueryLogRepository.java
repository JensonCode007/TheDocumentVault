package org.example.document_pro_v1.repository;

import org.example.document_pro_v1.entity.QueryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryLogRepository extends JpaRepository<QueryLog, String> {
    List<QueryLog> findAllByTenantIdOrderByQueriedAtDesc(Long tenant_id);
}
