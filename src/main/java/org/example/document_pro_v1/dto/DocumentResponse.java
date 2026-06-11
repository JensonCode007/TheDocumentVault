package org.example.document_pro_v1.dto;

import java.time.LocalDateTime;

public record DocumentResponse(

        Long id,
        String fileName,
        String uploadedBy,
        LocalDateTime uploadedAT,
        String tenantSlug


) {
}
