package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.resource.ResponseResource;
import com.worth.ifs.commons.rest.RestResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for CRUD operations on {@link Response} related data.
 */
public interface ResponseService {
    List<ResponseResource> getByApplication(Long applicationId);
    Map<Long, ResponseResource> mapResponsesToQuestion(List<ResponseResource> responses);
    RestResult<Void> saveQuestionResponseAssessorFeedback(Long assessorUserId, Long responseId, Optional<String> feedbackValue, Optional<String> feedbackText);
}
