package org.innovateuk.ifs.upload.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.springframework.core.io.ByteArrayResource;

public interface AttachmentRestService {
    RestResult<AttachmentResource> find(Long fileId);

    RestResult<AttachmentResource> upload(Long contextId, String contentType, long contentLength,
                                          String originalFilename, byte[] bytes);

    RestResult<Void> delete(Long id);

    RestResult<ByteArrayResource> download(Long fileId);
}
