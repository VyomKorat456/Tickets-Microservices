package com.auth_service.auth_service.service.serviceimpl;

import com.auth_service.auth_service.DTO.request.CreateUserRequestDTO;
import com.auth_service.auth_service.DTO.request.UpdateUserRequestDTO;
import com.auth_service.auth_service.DTO.response.UserDTO;
import com.auth_service.auth_service.entity.Users;
import com.auth_service.auth_service.mapper.UserMapper;
import com.auth_service.auth_service.repository.UserRepository;
import com.auth_service.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(CreateUserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("email already in use");
        }
        Users users = userMapper.toEntity(request);
        String hashPassword = request.getPassword();
        users.setPasswordHash(hashPassword);

        Users saved = userRepository.save(users);

        return userMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        Users users = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        return userMapper.toDTO(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    public UserDTO updateUser(Long id, UpdateUserRequestDTO request) {
        Users users = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("user not found"));

        userMapper.updateEntityFromDTO(request,users);
        Users updated = userRepository.save(users);
        return userMapper.toDTO(updated);
    }

    @Override
    public void deleteUser(Long id) {
        Users users = userRepository.findById(id).orElseThrow(()->new RuntimeException("user not found"));
        users.setActive(false);
        userRepository.save(users);
    }
}
