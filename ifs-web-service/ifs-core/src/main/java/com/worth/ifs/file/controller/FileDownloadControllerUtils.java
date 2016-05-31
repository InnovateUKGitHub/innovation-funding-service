package com.worth.ifs.file.controller;

import com.worth.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

/**
 * Utility class for helping Controllers with file downloads
 */
public class FileDownloadControllerUtils {

    public static ResponseEntity<ByteArrayResource> getFileResponseEntity(ByteArrayResource resource, FileEntryResource fileEntry) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(resource.contentLength());
        httpHeaders.setContentType(MediaType.parseMediaType(fileEntry.getMediaType()));
        if(StringUtils.hasText(fileEntry.getName())) {
        	httpHeaders.add("Content-Disposition", "attachment; filename=\"" + fileEntry.getName() + "\"");
        }
        return new ResponseEntity<>(resource, httpHeaders, HttpStatus.OK);
    }
}
