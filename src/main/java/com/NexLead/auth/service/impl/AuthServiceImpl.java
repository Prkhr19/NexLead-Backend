package com.NexLead.auth.service.impl;

import com.NexLead.auth.dto.AuthResponseDto;
import com.NexLead.auth.dto.LoginRequestDto;
import com.NexLead.auth.dto.RefreshTokenRequestDto;
import com.NexLead.auth.dto.RegisterRequestDto;
import com.NexLead.auth.entity.RefreshToken;
import com.NexLead.auth.entity.Role;
import com.NexLead.auth.entity.User;
import com.NexLead.auth.mapper.AuthMapper;
import com.NexLead.auth.repository.RefreshTokenRepository;
import com.NexLead.auth.repository.UserRepository;
import com.NexLead.auth.service.AuthService;
import com.NexLead.common.exception.DuplicateResourceException;
import com.NexLead.common.exception.InvalidTokenException;
import com.NexLead.config.JwtProperties;
import com.NexLead.security.CustomUserDetailsService;
import com.NexLead.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Override
    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        String email = request.getEmail().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email is already registered");
        }

        Role role = resolveRegistrationRole(request.getRole());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = authMapper.toEntity(request, encodedPassword, role);
        User savedUser = userRepository.save(user);

        return buildAuthResponse(savedUser);
    }

    @Override
    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()
                )
        );

        User user = userDetailsService.loadUserEntityByEmail(request.getEmail());
        refreshTokenRepository.deleteByUser(user);
        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token has expired");
        }

        User user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateAccessToken(
                userDetails,
                user.getId(),
                user.getRole().name()
        );

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpirationMs() / 1000)
                .user(authMapper.toUserResponseDto(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequestDto request) {
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);
    }

    private AuthResponseDto buildAuthResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateAccessToken(
                userDetails,
                user.getId(),
                user.getRole().name()
        );
        String refreshToken = createRefreshToken(user);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpirationMs() / 1000)
                .user(authMapper.toUserResponseDto(user))
                .build();
    }

    private String createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs()))
                .build();

        return refreshTokenRepository.save(refreshToken).getToken();
    }

    private Role resolveRegistrationRole(Role requestedRole) {
        if (userRepository.count() == 0) {
            return Role.ADMIN;
        }
        return requestedRole != null ? requestedRole : Role.STAFF;
    }
}
