package com.ticket_service.ticket_service.controller;

import com.ticket_service.ticket_service.DTO.projectDTO.request.ProjectCreateRequestDTO;
import com.ticket_service.ticket_service.DTO.projectDTO.response.ProjectResponseDTO;
import com.ticket_service.ticket_service.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ProjectResponseDTO createProject(@RequestBody ProjectCreateRequestDTO projectCreateRequestDTO) {
        return projectService.createProject(projectCreateRequestDTO);
    }

    @GetMapping
    public List<ProjectResponseDTO> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{projectId}")
    public ProjectResponseDTO getProjectById(@PathVariable Long projectId) {
        return projectService.getProjectById(projectId);
    }
}
