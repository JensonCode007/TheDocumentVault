package org.example.document_pro_v1.dto;

import java.util.List;
import java.util.Map;

public record SearchResponse(
        String query,
        String answer,
        List<Map<String, Object>> results

) {
}
