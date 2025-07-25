package com.techzen.techsale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {

    private int id;
    private String fileName;
    private int fileLength;
    private String extension;
    private String createBy;
    private String createAt;
}
