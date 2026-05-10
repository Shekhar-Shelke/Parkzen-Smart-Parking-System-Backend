package com.parkzen.controller;

import com.parkzen.dto.request.LoginRequest;
import com.parkzen.dto.request.OwnerRegisterRequest;
import com.parkzen.dto.request.UserRegisterRequest;
import com.parkzen.dto.response.ApiResponse;
import com.parkzen.dto.response.AuthResponse;
import com.parkzen.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/user/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", authService.registerUser(request)));
    }

    @PostMapping("/user/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginUser(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.loginUser(request)));
    }

    @PostMapping("/owner/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerOwner(@Valid @RequestBody OwnerRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Owner registered. Awaiting admin approval.", authService.registerOwner(request)));
    }

    @PostMapping("/owner/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginOwner(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.loginOwner(request)));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginAdmin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Admin login successful", authService.loginAdmin(request)));
    }
}
