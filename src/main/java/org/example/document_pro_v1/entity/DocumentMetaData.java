package org.example.document_pro_v1.entity;

import jakarta.persistence.*;

import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "document_metadata")
public class DocumentMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileHash; // SHA-256 Hash
    private String tenantId;
    private LocalDateTime uploadedAt;
}
