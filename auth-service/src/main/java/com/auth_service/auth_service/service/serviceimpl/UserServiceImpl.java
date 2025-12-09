package com.auth_service.auth_service.service.serviceimpl;

import com.auth_service.auth_service.DTO.request.CreateUserRequestDTO;
import com.auth_service.auth_service.DTO.request.LoginRequestDTO;
import com.auth_service.auth_service.DTO.request.UpdateUserRequestDTO;
import com.auth_service.auth_service.DTO.response.AuthResponseDTO;
import com.auth_service.auth_service.DTO.response.UserDTO;
import com.auth_service.auth_service.entity.Users;
import com.auth_service.auth_service.mapper.UserMapper;
import com.auth_service.auth_service.repository.UserRepository;
import com.auth_service.auth_service.security.JwtService;
import com.auth_service.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @Override
    public UserDTO createUser(CreateUserRequestDTO createUserRequestDTO) {
        if (userRepository.existsByEmail(createUserRequestDTO.getEmail())){
            throw new RuntimeException("email already in use");
        }
        Users users = userMapper.toEntity(createUserRequestDTO);
//        String hashPassword = createUserRequestDTO.getPassword();
        users.setPasswordHash(passwordEncoder.encode(createUserRequestDTO.getPassword()));

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
    public UserDTO updateUser(Long id, UpdateUserRequestDTO updateUserRequestDTO) {
        Users users = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("user not found"));

        userMapper.updateEntityFromDTO(updateUserRequestDTO,users);
        Users updated = userRepository.save(users);
        return userMapper.toDTO(updated);
    }

    @Override
    public void deleteUser(Long id) {
        Users users = userRepository.findById(id).orElseThrow(()->new RuntimeException("user not found"));
        users.setActive(false);
        userRepository.save(users);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {

        log.info("=== LOGIN REQUEST RECEIVED ===");
        log.info("Email : {}", request.getEmail());
        log.info("Password : {}", request.getPassword());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        log.info("auth success");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return AuthResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expireId(1800L) // optional, match your jwt.expiration-ms / 1000
                .build();
    }
}
