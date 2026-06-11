package org.example.document_pro_v1.dto;

import java.util.List;
import java.util.Map;

public record SearchResponse(
        String query,
        String tenantSlug,
        int n_results,
        List<ChatMessage> messageHistory

) {
}
