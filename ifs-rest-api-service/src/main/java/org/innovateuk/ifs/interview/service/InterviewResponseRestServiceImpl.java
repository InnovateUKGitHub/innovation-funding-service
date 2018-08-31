package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * REST service for managing applicant responses to interview feedback.
 */
@Service
public class InterviewResponseRestServiceImpl extends BaseRestService implements InterviewResponseRestService {

    private static final String INTERVIEW_RESPONSE_REST_URL = "/interview-response";

    @Override
    public RestResult<Void> uploadResponse(long applicationId, String contentType, long size, String originalFilename, byte[] multipartFileBytes) {
        String url = format("%s/%s?filename=%s", INTERVIEW_RESPONSE_REST_URL, applicationId, originalFilename);
        return postWithRestResult(url, multipartFileBytes, createFileUploadHeader(contentType, size), Void.class);
    }

    @Override
    public RestResult<Void> deleteResponse(long applicationId) {
        String url = format("%s/%s", INTERVIEW_RESPONSE_REST_URL, applicationId);
        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<ByteArrayResource> downloadResponse(long applicationId) {
        String url = format("%s/%s", INTERVIEW_RESPONSE_REST_URL, applicationId);
        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<FileEntryResource> findResponse(long applicationId) {
        String url = format("%s/%s/%s", INTERVIEW_RESPONSE_REST_URL, "details", applicationId);
        return getWithRestResult(url, FileEntryResource.class);
    }
}