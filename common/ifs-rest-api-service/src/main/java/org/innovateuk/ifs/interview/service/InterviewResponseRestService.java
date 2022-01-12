package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;

/**
 * REST service for managing applicant responses to interview feedback.
 */
public interface InterviewResponseRestService {

    RestResult<Void> uploadResponse(long applicationId, String contentType, long size, String originalFilename, byte[] multipartFileBytes);

    RestResult<Void> deleteResponse(long applicationId);

    RestResult<ByteArrayResource> downloadResponse(long applicationId);

    RestResult<FileEntryResource> findResponse(long applicationId);

}
