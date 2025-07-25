package com.techzen.techsale.validator;

import com.techzen.techsale.exception.BadRequestException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator implements Validator {

    private static final long MAX_FILE_SIZE_BYTES = (long) 1024 * 1024;

    private static final String[] FILE_EXTENSION_LIST = {"jpg", "jpeg", "png", "pdf", "docx", "xlsx"};

    @Override
    public boolean supports(Class<?> clazz) {
        return MultipartFile.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MultipartFile file = (MultipartFile) target;
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()) == null
            ? "" : FilenameUtils.getExtension(file.getOriginalFilename());
        boolean isValidExtension = ArrayUtils.contains(FILE_EXTENSION_LIST, fileExtension.toLowerCase());
        if (!isValidExtension) {
            throw new BadRequestException("errors: Only jpg, jpeg, png, docx, pdf, xlsx files are allowed.");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException("errors: File size must not exceed 1MB.");
        }
    }
}
