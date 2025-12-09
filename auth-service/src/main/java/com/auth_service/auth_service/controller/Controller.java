package com.auth_service.auth_service.controller;

import com.auth_service.auth_service.DTO.request.CreateUserRequestDTO;
import com.auth_service.auth_service.DTO.request.LoginRequestDTO;
import com.auth_service.auth_service.DTO.request.UpdateUserRequestDTO;
import com.auth_service.auth_service.DTO.response.AuthResponseDTO;
import com.auth_service.auth_service.DTO.response.UserDTO;
import com.auth_service.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class Controller {
    private final UserService userService;

    //user create
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequestDTO createUserRequestDTO){
        UserDTO userDTO = userService.createUser(createUserRequestDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(userService.login(loginRequestDTO));
    }
    //single user
    @GetMapping("/by/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    //all user
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    //update user
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequestDTO request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    //delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public String get(){
        return "Auth Service is Running with postgres";
    }
}
