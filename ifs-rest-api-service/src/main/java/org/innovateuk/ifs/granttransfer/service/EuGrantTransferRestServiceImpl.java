package org.innovateuk.ifs.granttransfer.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * REST service for managing interview panel invites.
 */
@Service
public class EuGrantTransferRestServiceImpl extends BaseRestService implements EuGrantTransferRestService {

    private static final String REST_URL = "/eu-grant-transfer";

    @Override
    public RestResult<Void> uploadGrantAgreement(long applicationId, String contentType, long size, String originalFilename, byte[] multipartFileBytes) {
        String url = format("%s/%s/%s?filename=%s", REST_URL, "grant-agreement", applicationId, originalFilename);
        return postWithRestResult(url, multipartFileBytes, createFileUploadHeader(contentType, size), Void.class);
    }

    @Override
    public RestResult<Void> deleteGrantAgreement(long applicationId) {
        String url = format("%s/%s/%s", REST_URL, "grant-agreement", applicationId);
        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<ByteArrayResource> downloadGrantAgreement(long applicationId) {
        String url = format("%s/%s/%s", REST_URL, "grant-agreement", applicationId);
        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<FileEntryResource> findGrantAgreement(long applicationId) {
        String url = format("%s/%s/%s", REST_URL, "grant-agreement-details", applicationId);
        return getWithRestResult(url, FileEntryResource.class);
    }

}