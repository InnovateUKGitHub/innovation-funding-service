package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.file.resource.FileEntryResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class AssessorFeedbackRestServiceImpl extends BaseRestService implements AssessorFeedbackRestService {

    @Value("${ifs.data.service.rest.assessorfeedback}")
    private String restUrl;

    @Override
    public RestResult<AssessorFeedbackResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, AssessorFeedbackResource.class);
    }

    @Override
    public RestResult<AssessorFeedbackResource> findByAssessorId(Long assessorId) {
        return getWithRestResult(restUrl + "/findByAssessor/" + assessorId, AssessorFeedbackResource.class);
    }

    @Override
    public RestResult<FileEntryResource> addAssessorFeedbackDocument(Long applicationId, String contentType, long contentLength, String originalFilename, byte[] file) {
        String url = restUrl + "/assessorFeedbackDocument?applicationId=" + applicationId + "&filename=" + originalFilename;
        return postWithRestResult(url, file, createHeader(contentType,  contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Void> removeAssessorFeedbackDocument(Long applicationId) {
        String url = restUrl + "/assessorFeedbackDocument?applicationId=" + applicationId;
        return deleteWithRestResult(url, Void.class);
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

    private HttpHeaders createHeader(String contentType, long contentLength){
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        return headers;
    }
}