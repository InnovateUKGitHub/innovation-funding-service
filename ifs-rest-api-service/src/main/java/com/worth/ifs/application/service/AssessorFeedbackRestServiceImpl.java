package com.worth.ifs.application.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.file.resource.FileEntryResource;
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

	@Override
	public RestResult<Boolean> feedbackUploaded(Long competitionId) {
		String url = restUrl + "/assessorFeedbackUploaded?competitionId=" + competitionId;
        return getWithRestResult(url, Boolean.class);
	}

	@Override
	public RestResult<Void> submitAssessorFeedback(Long competitionId) {
		String url = restUrl + "/submitAssessorFeedback/" + competitionId;
        return postWithRestResult(url, Void.class);
	}


}