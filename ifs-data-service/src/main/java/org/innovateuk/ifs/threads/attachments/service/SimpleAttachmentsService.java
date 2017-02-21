package org.innovateuk.ifs.threads.attachments.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SimpleAttachmentsService implements AttachmentsService {



    @Override
    public ServiceResult find(Long attachmentId) {
        return null;
    }

    @Override
    public ServiceResult upload(String contentType, String contentLength, String originalFilename, HttpServletRequest request) {
        return null;
    }

    @Override
    public ServiceResult<Void> delete(Long attachmentId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> download(Long attachmentId) throws IOException {
        return null;
    }
}
