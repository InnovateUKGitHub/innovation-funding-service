package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;

import java.util.List;
import java.util.Optional;

/**
 * Interface for CRUD operations on {@link Response} related data.
 */
public interface ResponseRestService {
    public List<Response> getResponsesByApplicationId(Long applicationId);
    public List<String> saveQuestionResponse(Long userId, Long applicationId, Long questionId, String value);
    public Boolean saveQuestionResponseAssessorFeedback(Long assessorUserId, Long responseId, Optional<String> feedbackValue, Optional<String> feedbackText);
}
