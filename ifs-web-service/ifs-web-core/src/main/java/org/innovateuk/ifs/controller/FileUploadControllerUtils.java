package org.innovateuk.ifs.controller;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.exception.UnableToReadUploadedFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Useful utility class for actions around the upload of files
 */
@Slf4j
public final class FileUploadControllerUtils {

    private FileUploadControllerUtils() {}

    public static byte[] getMultipartFileBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new UnableToReadUploadedFile();
        }
    }
}
