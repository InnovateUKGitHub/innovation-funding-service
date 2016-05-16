package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;

public interface AssessorFeedbackRestService {

    RestResult<AssessorFeedbackResource> findOne(Long id);
    RestResult<AssessorFeedbackResource> findByAssessorId(Long assessorId);

    RestResult<FileEntryResource> addAssessorFeedbackDocument(Long applicationId, String contentType, long contentLength, String originalFilename, byte[] file);

    RestResult<Void> removeAssessorFeedbackDocument(Long applicationId);

    RestResult<ByteArrayResource> getAssessorFeedbackFile(Long applicationId);
}