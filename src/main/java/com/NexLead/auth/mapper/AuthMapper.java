package com.NexLead.auth.mapper;

import com.NexLead.auth.dto.RegisterRequestDto;
import com.NexLead.auth.dto.UserResponseDto;
import com.NexLead.auth.entity.Role;
import com.NexLead.auth.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public User toEntity(RegisterRequestDto request, String encodedPassword, Role role) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .password(encodedPassword)
                .role(role)
                .enabled(true)
                .build();
    }

    public UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
