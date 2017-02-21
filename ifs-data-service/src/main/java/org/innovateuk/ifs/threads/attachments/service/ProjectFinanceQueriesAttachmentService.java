package org.innovateuk.ifs.threads.attachments.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface ProjectFinanceQueriesAttachmentService extends AttachmentsService<Attachment> {
    //TODO NUNO : @Override extended interface methods with target permissions

    @Override
    ServiceResult<Attachment> findOne(Long attachmentId);

    @Override
    ServiceResult<Attachment> upload(String contentType, String contentLength, String originalFilename, HttpServletRequest request);

    @Override
    ServiceResult<Void> delete(Long attachmentId);

    @Override
    ResponseEntity<Object> download(Long attachmentId) throws IOException;
}