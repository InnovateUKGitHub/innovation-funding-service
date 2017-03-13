package org.innovateuk.ifs.commons.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static java.util.Collections.singletonList;

public class FileUploadHeadersSet {
    final HttpHeaders headers;

    public FileUploadHeadersSet(String contentType, long contentLength) {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType(contentType)));
    }

    public HttpHeaders unwrap() {
        return headers;
    }
}
