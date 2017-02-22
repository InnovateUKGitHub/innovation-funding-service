package org.innovateuk.ifs.threads.attachments.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface ProjectFinanceAttachmentService extends AttachmentsService<AttachmentResource> {

    @Override
    @PostFilter("hasPermission(filterObject, 'PF_QUERY_ATTACHMENT_READ')")
    ServiceResult<AttachmentResource> findOne(Long attachmentId);

    @Override
    @PostFilter("hasPermission(filterObject, 'PF_ATTACHMENT_UPLOAD')")
    ServiceResult<AttachmentResource> upload(String contentType, String contentLength, String originalFilename, HttpServletRequest request);

    @Override
    @PreAuthorize("hasPermission(#attachmentId, 'org.innovateuk.threads.attachment.resource.AttachmentResource', 'PF_QUERY_ATTACHMENT_DELETE')")
    ServiceResult<Void> delete(Long attachmentId);

    @Override
    @PreAuthorize("hasPermission(#attachmentId, 'org.innovateuk.threads.attachment.resource.AttachmentResource', 'PF_QUERY_ATTACHMENT_DOWNLOAD')")
    ResponseEntity<Object> download(Long attachmentId) throws IOException;
}