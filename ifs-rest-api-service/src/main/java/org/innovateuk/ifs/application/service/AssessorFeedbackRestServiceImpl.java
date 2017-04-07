package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Service
public class AssessorFeedbackRestServiceImpl extends BaseRestService implements AssessorFeedbackRestService {

    private String restUrl = "/assessorfeedback";

    @Override
    public RestResult<FileEntryResource> addAssessorFeedbackDocument(Long applicationId, String contentType, long contentLength, String originalFilename, byte[] file) {
        String url = restUrl + "/assessorFeedbackDocument?applicationId=" + applicationId + "&filename=" + originalFilename;
        return postWithRestResult(url, file, createFileUploadHeader(contentType,  contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Void> removeAssessorFeedbackDocument(Long applicationId) {
        String url = restUrl + "/assessorFeedbackDocument?applicationId=" + applicationId;
        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<ByteArrayResource> getAssessorFeedbackFile(Long applicationId) {
        String url = restUrl + "/assessorFeedbackDocument?applicationId=" + applicationId;
        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<FileEntryResource> getAssessorFeedbackFileDetails(Long applicationId) {
        String url = restUrl + "/assessorFeedbackDocument/fileentry?applicationId=" + applicationId;
        return getWithRestResult(url, FileEntryResource.class);
    }
}
