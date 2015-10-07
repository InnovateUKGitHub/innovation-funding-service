package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface ResponseService {
    public List<Response> getByApplication(Long applicationId);
    public HashMap<Long, Response> mapResponsesToQuestion(List<Response> responses);
    public Boolean save(Long userId, Long applicationId, Long questionId, String value);
    public Boolean saveQuestionResponseAssessorFeedback(Long assessorUserId, Long responseId, Optional<String> feedbackValue, Optional<String> feedbackText);
}
