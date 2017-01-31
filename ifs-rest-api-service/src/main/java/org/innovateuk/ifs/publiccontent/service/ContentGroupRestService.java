package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;

/**
 * Rest service for public content.
 */
public interface ContentGroupRestService {


    RestResult<Void> uploadFile(Long groupId, String contentType, long contentLength, String originalFilename, byte[] file);

    RestResult<Void> removeFile(Long groupId);
}
