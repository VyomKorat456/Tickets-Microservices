package com.ticket_service.ticket_service.service.impl;

import com.ticket_service.ticket_service.DTO.projectDTO.request.ProjectCreateRequestDTO;
import com.ticket_service.ticket_service.DTO.projectDTO.response.ProjectResponseDTO;
import com.ticket_service.ticket_service.entitie.Project;
import com.ticket_service.ticket_service.mapper.TicketMapper;
import com.ticket_service.ticket_service.repository.ProjectRepository;
import com.ticket_service.ticket_service.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;

    @Override
    public ProjectResponseDTO createProject(ProjectCreateRequestDTO projectCreateRequestDTO) {
        if (projectRepository.existsByKey(projectCreateRequestDTO.getKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project key already exists");
        }

        Project project = Project.builder()
                .key(projectCreateRequestDTO.getKey())
                .name(projectCreateRequestDTO.getName())
                .description(projectCreateRequestDTO.getDescription())
                .build();

        return TicketMapper.toProjectResponseDTO(projectRepository.save(project));

    }

    @Override
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(TicketMapper::toProjectResponseDTO)
                .toList();
    }

    @Override
    public ProjectResponseDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        return TicketMapper.toProjectResponseDTO(project);
    }
}
