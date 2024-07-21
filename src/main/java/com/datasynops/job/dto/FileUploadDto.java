package com.datasynops.job.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileUploadDto {

        private String name;
        private Flux<DataBuffer> content;
        private String fileType;
        // getters and setters
}