package com.auth_service.auth_service.repository;

import com.auth_service.auth_service.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Long> {

    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);
}
