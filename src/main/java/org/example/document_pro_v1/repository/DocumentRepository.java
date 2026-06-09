package org.example.document_pro_v1.repository;

import org.example.document_pro_v1.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document,Long> {
    List<Document> findAllByTenantId(Long tenantId);

    Document findByFileName(String fileName);
}
