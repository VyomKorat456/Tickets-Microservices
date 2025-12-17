package com.auth_service.auth_service.service.serviceimpl;

import com.auth_service.auth_service.DTO.request.CreateUserRequestDTO;
import com.auth_service.auth_service.DTO.request.LoginRequestDTO;
import com.auth_service.auth_service.DTO.request.UpdateUserRequestDTO;
import com.auth_service.auth_service.DTO.response.AuthResponseDTO;
import com.auth_service.auth_service.DTO.response.UserDTO;
import com.auth_service.auth_service.email.EmailRequestDTO;
import com.auth_service.auth_service.email.EmailService;
import com.auth_service.auth_service.entity.Users;
import com.auth_service.auth_service.enums.Role;
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
import java.util.stream.Collectors;

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
    private final EmailService emailService;


    @Override
    public UserDTO createUser(CreateUserRequestDTO createUserRequestDTO) {
        if (userRepository.existsByEmail(createUserRequestDTO.getEmail())){
            throw new RuntimeException("email already in use");
        }
        Users users = userMapper.toEntity(createUserRequestDTO);
        users.setPasswordHash(passwordEncoder.encode(createUserRequestDTO.getPassword()));

        Users saved = userRepository.save(users);

        emailService.sendEmail(new EmailRequestDTO(){
            {
                setTo(saved.getEmail());
                setSubject("Account created Successfully");
                setBody(
                        "Hi "+ saved.getFullName()+ ",\n\n"+
                                "Your account has been created successfully. \n\n"+
                                "Email: "+ saved.getEmail()+"\n\n"+
                                "Password: "+createUserRequestDTO.getPassword()+"\n\n"+
                                "Role: "+saved.getRole() +"\n\n"+
                                "You can now login to the system. \n\n"+
                                "Thanks, \nSupport Team"
                );
            }
        });

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
        log.info("Password length: {}", request.getPassword().length());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            log.info("✓ Authentication successful");
            
            // Fetch the Users entity (which has role information)
            Users user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            log.info("User loaded: {}, Role: {}", user.getEmail(), user.getRole());
            
            // Generate token with role and userId claims
            String token = jwtService.generateToken(user);
            log.info("✓ JWT token generated for: {} with role: {}", user.getEmail(), user.getRole());

            return AuthResponseDTO.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expireId(1800L) // optional, match your jwt.expiration-ms / 1000
                    .build();
        } catch (Exception e) {
            log.error("✗ Authentication failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUserByRole(Role role) {
        List<Users> users =userRepository.findByRoleAndActiveTrue(role);
        if(users.isEmpty()){
            throw new RuntimeException("No users found "+role);
        }
        return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
}
