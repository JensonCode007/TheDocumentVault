package org.example.document_pro_v1.controller;

import lombok.RequiredArgsConstructor;
import org.example.document_pro_v1.dto.AuthResponse;
import org.example.document_pro_v1.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@)





}
