package com.attachment_service.attachment_service.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.jmx.export.annotation.ManagedNotifications;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadResponseDTO {
    private  Resource resource;
    private  String contentType;
    private  String fileName;
}
