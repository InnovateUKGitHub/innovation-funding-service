package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.resource.ResponseResource;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class contains methods to retrieve and store {@link Response} related data,
 * through the RestService {@link ResponseRestService}.
 */
// TODO DW - INFUND-1555 - handle rest results
@Service
public class ResponseServiceImpl implements ResponseService {

    @Autowired
    ResponseRestService responseRestService;

    @Override
    public List<ResponseResource> getByApplication(Long applicationId) {
        return responseRestService.getResponsesByApplicationId(applicationId).getSuccessObjectOrThrowException();
    }

    @Override
    public Map<Long, ResponseResource> mapResponsesToQuestion(List<ResponseResource> responses) {
        HashMap<Long, ResponseResource> responseMap = new HashMap<>();
        for (ResponseResource response : responses) {
            responseMap.put(response.getId(), response);
        }
        return responseMap;
    }

    @Override
    public RestResult<Void> saveQuestionResponseAssessorFeedback(Long assessorUserId, Long responseId, Optional<String> feedbackValue, Optional<String> feedbackText) {
        return responseRestService.saveQuestionResponseAssessorFeedback(assessorUserId, responseId, feedbackValue, feedbackText);
    }
}
