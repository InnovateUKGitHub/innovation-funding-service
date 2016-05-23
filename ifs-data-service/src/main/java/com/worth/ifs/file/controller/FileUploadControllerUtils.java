package com.worth.ifs.file.controller;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import static org.hibernate.jpa.internal.QueryImpl.LOG;

/**
 * Helpful utility methods for dealing with file uploads within Controllers
 */
public class FileUploadControllerUtils {

    public static Supplier<InputStream> inputStreamSupplier(HttpServletRequest request) {
        return () -> {
            try {
                return request.getInputStream();
            } catch (IOException e) {
                LOG.error("Unable to open an input stream from request", e);
                throw new RuntimeException("Unable to open an input stream from request", e);
            }
        };
    }
}
