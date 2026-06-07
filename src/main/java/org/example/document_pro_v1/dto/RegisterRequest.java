package org.example.document_pro_v1.dto;

public record RegisterRequest(
        String email,
        String password,
        String tenantSlug,
        String role
) {
}
