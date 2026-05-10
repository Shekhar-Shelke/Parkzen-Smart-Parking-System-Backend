package com.parkzen.service;

import com.parkzen.dto.request.LoginRequest;
import com.parkzen.dto.request.OwnerRegisterRequest;
import com.parkzen.dto.request.UserRegisterRequest;
import com.parkzen.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse registerUser(UserRegisterRequest request);
    AuthResponse loginUser(LoginRequest request);
    AuthResponse registerOwner(OwnerRegisterRequest request);
    AuthResponse loginOwner(LoginRequest request);
    AuthResponse loginAdmin(LoginRequest request);
}
