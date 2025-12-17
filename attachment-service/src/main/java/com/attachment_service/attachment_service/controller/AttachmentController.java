package com.attachment_service.attachment_service.controller;

import com.attachment_service.attachment_service.DTO.request.UploadAttachmentRequestDTO;
import com.attachment_service.attachment_service.DTO.response.AttachmentResponseDTO;
import com.attachment_service.attachment_service.DTO.response.DownloadResponseDTO;
import com.attachment_service.attachment_service.service.AttachmentService;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Validated
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponseDTO> upload(
            @RequestHeader(name = "Authorization") String authorization,
            @RequestParam("ticketId") @NotNull(message = "ticketId is required") @Positive(message = "ticketId must be positive") Long ticketId,
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        UploadAttachmentRequestDTO req = new UploadAttachmentRequestDTO();
        req.setTicketId(ticketId);
        AttachmentResponseDTO dto = attachmentService.upload(authorization, req, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/preview/{attachmentId}")
    public ResponseEntity<Resource> preview(
            @PathVariable @NotNull @Positive Long attachmentId
    ) throws Exception {

        DownloadResponseDTO dto = attachmentService.download(attachmentId);

        if (dto == null || dto.getResource() == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = dto.getContentType() != null
                ? dto.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                // 👇 INLINE instead of attachment
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + dto.getFileName() + "\"")
                .body(dto.getResource());
    }

    @GetMapping("/by-ticket/{ticketId}")
    public ResponseEntity<List<AttachmentResponseDTO>> listByTicket(@PathVariable @NotNull(message = "ticketId is required") @Positive(message = "ticketId must be positive") Long ticketId) {
        List<AttachmentResponseDTO> list = attachmentService.listByTicket(ticketId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> download(@PathVariable @NotNull(message = "attachmentId is required") @Positive(message = "attachmentId must be positive") Long attachmentId) throws Exception {
        DownloadResponseDTO downloadResponseDTO = attachmentService.download(attachmentId);
        if (downloadResponseDTO == null || downloadResponseDTO.getResource() == null) {
            return ResponseEntity.notFound().build();
        }
        String ct = downloadResponseDTO.getContentType() != null ? downloadResponseDTO.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(ct))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadResponseDTO.getFileName() + "\"")
                .body(downloadResponseDTO.getResource());
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> delete(
            @RequestHeader(name = "Authorization") String authorization,
            @PathVariable @NotNull(message = "attachmentId is required") @Positive(message = "attachmentId must be positive") Long attachmentId) throws Exception {
        attachmentService.delete(authorization, attachmentId);
        return ResponseEntity.noContent().build();
    }

}
