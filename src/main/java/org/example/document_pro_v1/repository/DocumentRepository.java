package org.example.document_pro_v1.repository;

import org.example.document_pro_v1.entity.Document;
import org.example.document_pro_v1.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document,Long> {
    List<Document> findAllByTenantId(Long tenantId);

    Document findByFileName(String fileName);



    boolean existsByTenantAndFileHash(Tenant tenant, String filehash);

    Page<Document> findByTenant(Tenant tenant, Pageable pageable);
}
