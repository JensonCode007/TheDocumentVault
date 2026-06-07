package org.example.document_pro_v1.repository;

import org.example.document_pro_v1.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document,Long> {
    List<Document> findAllByTenantId(String tenantId);
}
