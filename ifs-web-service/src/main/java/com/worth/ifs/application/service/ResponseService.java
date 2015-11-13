package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.validator.ValidatedResponse;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Interface for CRUD operations on {@link Response} related data.
 */
public interface ResponseService {
    public List<Response> getByApplication(Long applicationId);
    public HashMap<Long, Response> mapResponsesToQuestion(List<Response> responses);
    public List<String> save(Long userId, Long applicationId, Long questionId, String value);
    public Boolean saveQuestionResponseAssessorFeedback(Long assessorUserId, Long responseId, Optional<String> feedbackValue, Optional<String> feedbackText);
}
