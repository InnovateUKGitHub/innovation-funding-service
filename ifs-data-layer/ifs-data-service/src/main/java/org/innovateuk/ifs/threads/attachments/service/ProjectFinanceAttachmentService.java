package org.innovateuk.ifs.threads.attachments.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;

public interface ProjectFinanceAttachmentService extends AttachmentsService<AttachmentResource> {

    @Override
    @PostAuthorize("hasPermission(returnObject, 'PF_ATTACHMENT_READ')")
    ServiceResult<AttachmentResource> findOne(Long attachmentId);

    @Override
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'PF_ATTACHMENT_UPLOAD')")
    ServiceResult<AttachmentResource> upload(String contentType, String contentLength, String originalFilename,
                                             @P("projectId") final Long projectId, HttpServletRequest request);

    @Override
    @PreAuthorize("hasPermission(#attachmentId, 'org.innovateuk.threads.attachment.resource.AttachmentResource', 'PF_ATTACHMENT_DELETE')")
    ServiceResult<Void> delete(Long attachmentId);

    @Override
    @PreAuthorize("hasPermission(#attachmentId, 'org.innovateuk.threads.attachment.resource.AttachmentResource', 'PF_ATTACHMENT_DOWNLOAD')")
    ServiceResult<FileAndContents> attachmentFileAndContents(Long attachmentId);
}