package org.example.document_pro_v1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.document_pro_v1.dto.AuthResponse;
import org.example.document_pro_v1.dto.LoginRequest;
import org.example.document_pro_v1.dto.RegisterRequest;
import org.example.document_pro_v1.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody RegisterRequest registerRequest) {
        log.info("Signup request received from : {}", registerRequest.email());
        AuthResponse response = authService.signUpRequest(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login request received from : {}", loginRequest.email());
        AuthResponse response = authService.loginRequest(loginRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }





}
