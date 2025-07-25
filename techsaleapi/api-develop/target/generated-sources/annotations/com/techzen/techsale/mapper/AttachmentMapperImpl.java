package com.techzen.techsale.mapper;

import com.techzen.techsale.dto.AttachmentDTO;
import com.techzen.techsale.entity.AttachmentsEntity;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-25T11:45:05+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class AttachmentMapperImpl implements AttachmentMapper {

    @Override
    public AttachmentDTO toAttachmentDTO(AttachmentsEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AttachmentDTO.AttachmentDTOBuilder attachmentDTO = AttachmentDTO.builder();

        attachmentDTO.id( entity.getId() );
        attachmentDTO.fileName( entity.getFileName() );
        if ( entity.getFileLength() != null ) {
            attachmentDTO.fileLength( entity.getFileLength().intValue() );
        }
        attachmentDTO.extension( entity.getExtension() );
        if ( entity.getCreateAt() != null ) {
            attachmentDTO.createAt( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( entity.getCreateAt() ) );
        }

        return attachmentDTO.build();
    }
}
