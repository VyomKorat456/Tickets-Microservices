package com.ticket_service.ticket_service.service;

import com.ticket_service.ticket_service.DTO.projectDTO.request.ProjectCreateRequestDTO;
import com.ticket_service.ticket_service.DTO.projectDTO.response.ProjectResponseDTO;

import java.util.List;

public interface ProjectService {
    ProjectResponseDTO createProject(ProjectCreateRequestDTO projectCreateRequestDTO);

    List<ProjectResponseDTO> getAllProjects();

    ProjectResponseDTO getProjectById(Long id);
}
