package com.auth_service.auth_service.mapper;

import com.auth_service.auth_service.DTO.request.CreateUserRequestDTO;
import com.auth_service.auth_service.DTO.request.UpdateUserRequestDTO;
import com.auth_service.auth_service.DTO.response.UserDTO;
import com.auth_service.auth_service.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public Users toEntity(CreateUserRequestDTO createUserRequestDTO){
        if (createUserRequestDTO==null) return null;
        return Users.builder()
                .fullName(createUserRequestDTO.getFullName())
                .email(createUserRequestDTO.getEmail())
                .role(createUserRequestDTO.getRole())
                .active(true)
                .build();
    };

    public UserDTO toDTO(Users users){
        if (users == null) return null;
        return UserDTO.builder()
                .id(users.getId())
                .fullName(users.getFullName())
                .email(users.getEmail())
                .role(users.getRole())
                .active(users.isActive())
                .createdAt(users.getCreatedAt())
                .updatedAt(users.getUpdatedAt())
                .build();
    };

    public void updateEntityFromDTO(UpdateUserRequestDTO updateUserRequestDTO, Users users){
        if(updateUserRequestDTO == null) return;
        if (updateUserRequestDTO.getFullName() != null)
            users.setFullName(updateUserRequestDTO.getFullName());

        if (updateUserRequestDTO.getEmail() != null)
            users.setEmail(updateUserRequestDTO.getEmail());

        if (updateUserRequestDTO.getRole() != null)
            users.setRole(updateUserRequestDTO.getRole());

        if (updateUserRequestDTO.getActive() != null)
            users.setActive(updateUserRequestDTO.getActive());
    };
}
