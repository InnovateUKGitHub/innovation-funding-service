package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

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
    public Boolean save(Long userId, Long applicationId, Long questionId, String value) {
        return responseRestService.saveQuestionResponse(userId, applicationId, questionId, value);
    }

    @Override
    public Boolean saveQuestionResponseAssessorScore(Long assessorUserId, Long responseId, Integer score) {
        return responseRestService.saveQuestionResponseAssessorScore(assessorUserId, responseId, score);
    }

    @Override
    public Boolean saveQuestionResponseAssessorConfirmationAnswer(Long assessorUserId, Long responseId, Boolean confirmation) {
        return responseRestService.saveQuestionResponseAssessorConfirmationAnswer(assessorUserId, responseId, confirmation);
    }

    @Override
    public Boolean saveQuestionResponseAssessorFeedback(Long assessorUserId, Long responseId, String feedback) {
        return responseRestService.saveQuestionResponseAssessorFeedback(assessorUserId, responseId, feedback);
    }
}
