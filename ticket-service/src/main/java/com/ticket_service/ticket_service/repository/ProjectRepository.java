package com.ticket_service.ticket_service.repository;

import com.ticket_service.ticket_service.entitie.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByKey(String key);
}
