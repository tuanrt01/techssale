package com.techzen.techsale.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    @Value("${techsale.attachments-path}")
    private String attachmentsPath;
    public byte[] getFile(String filename) {
        String filePath = attachmentsPath.replaceAll("\\%d_\\%s_\\%s \\%s\\.\\%s", "") + filename;
        try {
            Path path = Paths.get(filePath);
            byte[] fileContent = Files.readAllBytes(path);
            return fileContent;
        } catch (InvalidPathException | IOException invalidPathException) {
            log.error(invalidPathException.getMessage());
            return null;
        }
    }
}
