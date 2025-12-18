package com.attachment_service.attachment_service.service.impl;

import com.attachment_service.attachment_service.DTO.request.UploadAttachmentRequestDTO;
import com.attachment_service.attachment_service.DTO.response.AttachmentResponseDTO;
import com.attachment_service.attachment_service.DTO.response.DownloadResponseDTO;
import com.attachment_service.attachment_service.entity.TicketAttachment;
import com.attachment_service.attachment_service.exception.UnauthorizedException;
import com.attachment_service.attachment_service.mapper.AttachmentMapper;
import com.attachment_service.attachment_service.repository.TicketAttachmentRepository;
import com.attachment_service.attachment_service.security.JwtUtil;
import com.attachment_service.attachment_service.service.AttachmentService;
import com.attachment_service.attachment_service.service.LocalDiskStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
            throw new com.attachment_service.attachment_service.exception.UnauthorizedException("Invalid or missing token");
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
            .orElseThrow(() -> new com.attachment_service.attachment_service.exception.ResourceNotFoundException("Attachment not found: " + id));
        return attachmentMapper.toDto(t);
    }

    @Override
    public void delete(String authorizationHeader, Long id) throws Exception {
        String token = extractToken(authorizationHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new com.attachment_service.attachment_service.exception.UnauthorizedException("Invalid or missing token");
        }
        Long userId = jwtUtil.extractUserId(token);

        TicketAttachment t = repository.findById(id)
            .orElseThrow(() -> new com.attachment_service.attachment_service.exception.ResourceNotFoundException("Attachment not found: " + id));

        boolean isOwner = userId != null && userId.equals(t.getUploadedByUserId());
        boolean isAdmin = jwtUtil.extractRoles(token).stream()
                .anyMatch(r -> r.equalsIgnoreCase("ADMIN") || r.equalsIgnoreCase("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) throw new com.attachment_service.attachment_service.exception.UnauthorizedException("Not allowed to delete");

        repository.delete(t);
        java.nio.file.Files.deleteIfExists(storage.resolve(t.getFilePath()));
    }

    @Override
    public DownloadResponseDTO download(Long attachmentId) throws Exception {
        // 1. Get attachment record
        TicketAttachment attachment = repository.findById(attachmentId)
                .orElseThrow(() ->
                        new com.attachment_service.attachment_service.exception.ResourceNotFoundException(
                                "Attachment not found: " + attachmentId
                        )
                );

        // 2. Resolve file path from disk
        Path filePath = storage.resolve(attachment.getFilePath());

        if (!java.nio.file.Files.exists(filePath)) {
            throw new com.attachment_service.attachment_service.exception.ResourceNotFoundException(
                    "File not found on disk"
            );
        }

        // 3. Create Spring Resource
        Resource resource = new FileSystemResource(filePath);

        // 4. Build response DTO
        DownloadResponseDTO dto = new DownloadResponseDTO();
        dto.setResource(resource);
        dto.setFileName(attachment.getFileName());
        dto.setContentType(
                attachment.getContentType() != null
                        ? attachment.getContentType()
                        : MediaType.APPLICATION_OCTET_STREAM_VALUE
        );

        return dto;
    }

    @Override
    @Transactional
    public AttachmentResponseDTO update(
            String authorizationHeader,
            Long attachmentId,
            MultipartFile newFile
    ) throws Exception {

        String token = extractToken(authorizationHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new UnauthorizedException("Invalid or missing token");
        }

        Long userId = jwtUtil.extractUserId(token);
        boolean isAdmin = jwtUtil.extractRoles(token).stream()
                .anyMatch(r -> r.equalsIgnoreCase("ADMIN") || r.equalsIgnoreCase("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new UnauthorizedException("Only admin can update ticket attachment");
        }

    /* ===============================
       1️⃣ FIND EXISTING ATTACHMENT
    =============================== */
        TicketAttachment existing = repository.findById(attachmentId)
                .orElseThrow(() ->
                        new RuntimeException("Attachment not found: " + attachmentId)
                );

        Long ticketId = existing.getTicketId();

        log.info("Updating attachment for ticketId={}", ticketId);

    /* ===============================
       2️⃣ FIND ALL ATTACHMENTS FOR TICKET
    =============================== */
        List<TicketAttachment> oldAttachments =
                repository.findByTicketId(ticketId);

        log.info("Found {} existing attachments for ticketId={}",
                oldAttachments.size(), ticketId);

    /* ===============================
       3️⃣ DELETE OLD FILES (DISK)
    =============================== */
        for (TicketAttachment att : oldAttachments) {
            try {
                Path path = storage.resolve(att.getFilePath());
                boolean deleted = Files.deleteIfExists(path);

                log.info(
                        "Deleted old file | attachmentId={} | path={} | deleted={}",
                        att.getId(),
                        path.toAbsolutePath(),
                        deleted
                );
            } catch (Exception ex) {
                log.error(
                        "Failed to delete file for attachmentId={}",
                        att.getId(),
                        ex
                );
            }
        }

    /* ===============================
       4️⃣ DELETE OLD DB RECORDS
    =============================== */
        repository.deleteAll(oldAttachments);
        log.info("Deleted old attachment DB records for ticketId={}", ticketId);

    /* ===============================
       5️⃣ STORE NEW FILE
    =============================== */
        String newRelativePath = storage.store(ticketId, newFile);

        log.info("Stored new file for ticketId={} at path={}",
                ticketId, newRelativePath);

    /* ===============================
       6️⃣ SAVE NEW ATTACHMENT ROW
    =============================== */
        TicketAttachment newEntity = TicketAttachment.builder()
                .ticketId(ticketId)
                .fileName(newFile.getOriginalFilename())
                .filePath(newRelativePath)
                .fileSize(newFile.getSize())
                .contentType(newFile.getContentType())
                .uploadedByUserId(userId)
                .uploadedAt(Instant.now())
                .build();

        TicketAttachment saved = repository.save(newEntity);

        log.info("New attachment saved successfully. attachmentId={}", saved.getId());

        return attachmentMapper.toDto(saved);
    }




    //helper
    private String extractToken(String header) {
        if (header == null) return null;
        return header.startsWith("Bearer ") ? header.substring(7) : header;
    }
}
