package com.blooddonation.controller;

import com.blooddonation.dto.request.RegisterRequest;
import com.blooddonation.dto.request.LoginRequest;
import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.dto.response.AuthResponse;

import com.blooddonation.service.AuthService;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest req) {

        return ResponseEntity.ok(
                authService.register(req)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest req) {

        return ResponseEntity.ok(
                authService.login(req)
        );
    }
}