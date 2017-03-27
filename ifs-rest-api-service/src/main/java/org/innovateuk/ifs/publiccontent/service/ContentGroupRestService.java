package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;

/**
 * Rest service for public content.
 */
public interface ContentGroupRestService {

    RestResult<Void> uploadFile(Long groupId, String contentType, long contentLength, String originalFilename, byte[] file);

    RestResult<Void> removeFile(Long groupId);

    RestResult<ByteArrayResource> getFile(Long groupId);

    RestResult<FileEntryResource> getFileDetails(Long contentGroupId);

    RestResult<ByteArrayResource> getFileAnonymous(Long groupId);

    RestResult<FileEntryResource> getFileDetailsAnonymous(Long contentGroupId);
}
