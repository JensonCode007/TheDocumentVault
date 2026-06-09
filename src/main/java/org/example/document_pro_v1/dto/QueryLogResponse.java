package org.example.document_pro_v1.dto;

import java.time.LocalDateTime;

public record QueryLogResponse(
        Long id,
        String query,
        String queryResponse,
        String responseTimeMS,
        LocalDateTime queriedAt,
        String userEmail

) {
}
