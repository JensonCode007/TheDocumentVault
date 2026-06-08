package org.example.document_pro_v1.dto;

import org.example.document_pro_v1.Enums.Role;

public record RegisterRequest(
        String email,
        String password,
        String tenantSlug,
        Role role
) {
}
