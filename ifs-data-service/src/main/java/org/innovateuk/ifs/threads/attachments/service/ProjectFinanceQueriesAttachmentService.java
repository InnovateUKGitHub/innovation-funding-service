package org.innovateuk.ifs.threads.attachments.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface ProjectFinanceQueriesAttachmentService extends AttachmentsService<Attachment> {

    @Override
    @PostFilter("hasPermission(filterObject, 'PF_QUERY_ATTACHMENT_READ')")
    ServiceResult<Attachment> findOne(Long attachmentId);

    @Override
    @PostFilter("hasPermission(filterObject, 'PF_ATTACHMENT_READ')")
    @PreAuthorize("hasPermission('PF_QUERY_ATTACHMENT_UPLOAD')")//TODO Nuno: how to not have any parameter
    ServiceResult<Attachment> upload(String contentType, String contentLength, String originalFilename, HttpServletRequest request);

    @Override
    @PreAuthorize("hasPermission(#attachmentId, 'org.innovateuk.threads.attachments.resource.AttachmentResource', 'PF_QUERY_ATTACHMENT_DELETE')")
    ServiceResult<Void> delete(Long attachmentId);

    @Override
    @PreAuthorize("hasPermission(#attachmentId, 'org.innovateuk.threads.attachments.resource.AttachmentResource', 'PF_QUERY_ATTACHMENT_DOWNLOAD')")
    ResponseEntity<Object> download(Long attachmentId) throws IOException;
}