package com.techzen.techsale.service.impl;

import com.techzen.techsale.entity.AttachmentsEntity;
import com.techzen.techsale.entity.RequestApplicationEntity;
import com.techzen.techsale.exception.ForbiddenException;
import com.techzen.techsale.exception.NotFoundException;
import com.techzen.techsale.repository.AttachmentsRepository;
import com.techzen.techsale.repository.IRequestApplicationRepository;
import com.techzen.techsale.service.AttachmentsService;
import com.techzen.techsale.utils.SecurityUtils;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentsServiceImpl implements AttachmentsService {

    private final SecurityUtils securityUtils;
    private final IRequestApplicationRepository requestApplicationRepository;
    private final AttachmentsRepository attachmentsRepository;

    @Override
    public byte[] getAttachments(int fileId) throws IOException {
        log.info("Start get attachments by id: {}", fileId);
        AttachmentsEntity attachment = attachmentsRepository.findById(fileId)
            .orElseThrow(() -> new NotFoundException("Not found file id: " + fileId));

        if (!isOwner(attachment.getRequestId())) {
            throw new ForbiddenException("Not have permission to access this resource!");
        }

        File file = new File(attachment.getFilePath());
        if (file.exists()) {
            return FileUtils.readFileToByteArray(file);
        }

        throw new NotFoundException(String.format("Not found file id: %s on disk! ", fileId));
    }

    private boolean isOwner(int requestId) {
        RequestApplicationEntity request = requestApplicationRepository.findById(requestId)
            .orElseThrow(() -> new NotFoundException("Not found Request Application by id: " + requestId));

        return securityUtils.isAdmin() ||
               securityUtils.isBuyer() ||
               securityUtils.getCurrentUserInfo().getId().equals(request.getRequestUserId());
    }
}
