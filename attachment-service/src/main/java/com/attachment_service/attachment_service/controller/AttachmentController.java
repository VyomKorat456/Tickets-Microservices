package com.attachment_service.attachment_service.controller;

import com.attachment_service.attachment_service.DTO.request.UploadAttachmentRequestDTO;
import com.attachment_service.attachment_service.DTO.response.AttachmentResponseDTO;
import com.attachment_service.attachment_service.DTO.response.DownloadResponseDTO;
import com.attachment_service.attachment_service.service.AttachmentService;
import io.opentelemetry.sdk.resources.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponseDTO> upload(
            @RequestHeader(name = "Authorization") String authorization,
            @RequestParam("ticketId") Long ticketId,
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        UploadAttachmentRequestDTO req = new UploadAttachmentRequestDTO();
        req.setTicketId(ticketId);
        AttachmentResponseDTO dto = attachmentService.upload(authorization, req, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/by-ticket/{ticketId}")
    public ResponseEntity<List<AttachmentResponseDTO>> listByTicket(@PathVariable Long ticketId) {
        List<AttachmentResponseDTO> list = attachmentService.listByTicket(ticketId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> download(@PathVariable Long attachmentId) throws Exception {
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
            @PathVariable Long attachmentId) throws Exception {
        attachmentService.delete(authorization, attachmentId);
        return ResponseEntity.noContent().build();
    }

}
