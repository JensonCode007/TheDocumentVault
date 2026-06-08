package org.example.document_pro_v1.dto;

import org.example.document_pro_v1.Enums.Role;

public record AuthResponse(
        String token,
        String email,
        Role role,
        Long tenantId,
        String tenantSlug


) {
}
