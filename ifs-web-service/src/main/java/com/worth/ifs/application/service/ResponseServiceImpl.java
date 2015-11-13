package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * This class contains methods to retrieve and store {@link Response} related data,
 * through the RestService {@link ResponseRestService}.
 */
@Service
public class ResponseServiceImpl implements ResponseService {

    @Autowired
    ResponseRestService responseRestService;

    @Override
    public List<Response> getByApplication(Long applicationId) {
        return responseRestService.getResponsesByApplicationId(applicationId);
    }

    @Override
    public HashMap<Long, Response> mapResponsesToQuestion(List<Response> responses) {
        HashMap<Long, Response> responseMap = new HashMap<>();
        for (Response response : responses) {
            responseMap.put(response.getQuestion().getId(), response);
        }
        return responseMap;
    }

    @Override
    public List<String> save(Long userId, Long applicationId, Long questionId, String value) {
        List<String> validated = responseRestService.saveQuestionResponse(userId, applicationId, questionId, value);
        return validated;
    }

    @Override
    public Boolean saveQuestionResponseAssessorFeedback(Long assessorUserId, Long responseId, Optional<String> feedbackValue, Optional<String> feedbackText) {
        return responseRestService.saveQuestionResponseAssessorFeedback(assessorUserId, responseId, feedbackValue, feedbackText);
    }
}
