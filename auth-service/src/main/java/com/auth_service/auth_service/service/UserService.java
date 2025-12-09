package com.auth_service.auth_service.service;

import com.auth_service.auth_service.DTO.request.CreateUserRequestDTO;
import com.auth_service.auth_service.DTO.request.LoginRequestDTO;
import com.auth_service.auth_service.DTO.request.UpdateUserRequestDTO;
import com.auth_service.auth_service.DTO.response.AuthResponseDTO;
import com.auth_service.auth_service.DTO.response.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(CreateUserRequestDTO request);

    UserDTO getUserById(Long id);

    List<UserDTO> getAllUsers();

    UserDTO updateUser(Long id, UpdateUserRequestDTO request);

    void deleteUser(Long id);

    AuthResponseDTO login(LoginRequestDTO request);
}
