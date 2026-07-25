package com.NexLead.auth.service;

import com.NexLead.auth.dto.AuthResponseDto;
import com.NexLead.auth.dto.LoginRequestDto;
import com.NexLead.auth.dto.RefreshTokenRequestDto;
import com.NexLead.auth.dto.RegisterRequestDto;

public interface AuthService {

    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);

    AuthResponseDto refreshToken(RefreshTokenRequestDto request);

    void logout(RefreshTokenRequestDto request);
}
