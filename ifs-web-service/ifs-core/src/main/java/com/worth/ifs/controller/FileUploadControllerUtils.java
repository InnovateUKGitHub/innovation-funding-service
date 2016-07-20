package com.worth.ifs.controller;

import com.worth.ifs.exception.UnableToReadUploadedFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Useful utility class for actions around the upload of files
 */
public class FileUploadControllerUtils {

    private static final Log LOG = LogFactory.getLog(FileUploadControllerUtils.class);

    public static byte[] getMultipartFileBytes(MultipartFile file) {
        try {

            return file.getBytes();

        } catch (IOException e) {
            LOG.error(e);
            throw new UnableToReadUploadedFile();
        }
    }
}
