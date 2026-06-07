package org.example.document_pro_v1.dto;

public record LoginRequest(
        String email,
        String password
) {
}
