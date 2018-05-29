package org.innovateuk.ifs.controller;

import org.innovateuk.ifs.exception.UnableToReadUploadedFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Useful utility class for actions around the upload of files
 */
public final class FileUploadControllerUtils {

    private static final Log LOG = LogFactory.getLog(FileUploadControllerUtils.class);

    private FileUploadControllerUtils() {}

    public static byte[] getMultipartFileBytes(MultipartFile file) {
        try {

            return file.getBytes();

        } catch (IOException e) {
            LOG.error(e);
            throw new UnableToReadUploadedFile();
        }
    }
}
