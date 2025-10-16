package com.notes.api.service;

import com.notes.api.web.dto.AuthResponse;
import com.notes.api.web.dto.LoginRequest;
import com.notes.api.web.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}