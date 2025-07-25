package com.techzen.techsale.mapper;

import com.techzen.techsale.dto.AttachmentDTO;
import com.techzen.techsale.entity.AttachmentsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    AttachmentDTO toAttachmentDTO(AttachmentsEntity entity);
}
