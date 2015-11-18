package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for CRUD operations on {@link Response} related data.
 */
public interface ResponseService {
    List<Response> getByApplication(Long applicationId);
    Map<Long, Response> mapResponsesToQuestion(List<Response> responses);
    Boolean saveQuestionResponseAssessorFeedback(Long assessorUserId, Long responseId, Optional<String> feedbackValue, Optional<String> feedbackText);
}
