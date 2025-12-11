package com.attachment_service.attachment_service.service;

import com.attachment_service.attachment_service.DTO.request.UploadAttachmentRequestDTO;
import com.attachment_service.attachment_service.DTO.response.AttachmentResponseDTO;
import com.attachment_service.attachment_service.DTO.response.DownloadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {

    AttachmentResponseDTO upload(String authorizationHeader, UploadAttachmentRequestDTO uploadAttachmentRequestDTO, MultipartFile file) throws Exception;

    List<AttachmentResponseDTO> listByTicket(Long ticketId);

    AttachmentResponseDTO getById(Long id);

    void delete(String authorizationHeader, Long id) throws Exception;

    DownloadResponseDTO download(Long attachmentId) throws Exception;

}
