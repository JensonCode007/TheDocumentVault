package org.example.document_pro_v1.dto;

public record AuthResponse(
        String token,
        String email,
        String role,
        String tenantId


) {
}
