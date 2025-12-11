package com.attachment_service.attachment_service.service.impl;

import com.attachment_service.attachment_service.DTO.request.UploadAttachmentRequestDTO;
import com.attachment_service.attachment_service.DTO.response.AttachmentResponseDTO;
import com.attachment_service.attachment_service.DTO.response.DownloadResponseDTO;
import com.attachment_service.attachment_service.entity.TicketAttachment;
import com.attachment_service.attachment_service.mapper.AttachmentMapper;
import com.attachment_service.attachment_service.repository.TicketAttachmentRepository;
import com.attachment_service.attachment_service.security.JwtUtil;
import com.attachment_service.attachment_service.service.AttachmentService;
import com.attachment_service.attachment_service.service.LocalDiskStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final TicketAttachmentRepository repository;
    private final LocalDiskStorageService storage;
    private final JwtUtil jwtUtil;
    private final AttachmentMapper attachmentMapper;

    @Override
    public AttachmentResponseDTO upload(String authorizationHeader, UploadAttachmentRequestDTO uploadAttachmentRequestDTO, MultipartFile file) throws Exception {
        String token = extractToken(authorizationHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new RuntimeException("Invalid or missing token");
        }
        Long userId = jwtUtil.extractUserId(token);

        String relativePath = storage.store(uploadAttachmentRequestDTO.getTicketId(), file);

        TicketAttachment entity = TicketAttachment.builder()
                .ticketId(uploadAttachmentRequestDTO.getTicketId())
                .fileName(file.getOriginalFilename())
                .filePath(relativePath)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .uploadedByUserId(userId)
                .uploadedAt(Instant.now())
                .build();

        TicketAttachment saved = repository.save(entity);
        return attachmentMapper.toDto(saved);
    }

    @Override
    public List<AttachmentResponseDTO> listByTicket(Long ticketId) {
        List<AttachmentResponseDTO> attachmentResponseDTOList = repository.findByTicketIdOrderByUploadedAtAsc(ticketId)
                .stream()
                .map(attachmentMapper::toDto)
                .collect(Collectors.toList());
        return attachmentResponseDTOList;
    }

    @Override
    public AttachmentResponseDTO getById(Long id) {
        TicketAttachment t = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found: " + id));
        return attachmentMapper.toDto(t);
    }

    @Override
    public void delete(String authorizationHeader, Long id) throws Exception {
        String token = extractToken(authorizationHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new RuntimeException("Invalid or missing token");
        }
        Long userId = jwtUtil.extractUserId(token);

        TicketAttachment t = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found: " + id));

        boolean isOwner = userId != null && userId.equals(t.getUploadedByUserId());
        boolean isAdmin = jwtUtil.extractRoles(token).stream()
                .anyMatch(r -> r.equalsIgnoreCase("ADMIN") || r.equalsIgnoreCase("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) throw new RuntimeException("Not allowed to delete");

        repository.delete(t);
        java.nio.file.Files.deleteIfExists(storage.resolve(t.getFilePath()));
    }

    @Override
    public DownloadResponseDTO download(Long attachmentId) throws Exception {
        return null;
    }

    //helper
    private String extractToken(String header) {
        if (header == null) return null;
        return header.startsWith("Bearer ") ? header.substring(7) : header;
    }
}
