package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

/**
 * ResponseRestServiceImpl is a utility for CRUD operations on {@link Response}'s.
 * This class connects to the {@link com.worth.ifs.application.controller.ResponseController}
 * through a REST call.
 */
@Service
public class ResponseRestServiceImpl extends BaseRestService implements ResponseRestService {

    @Value("${ifs.data.service.rest.response}")
    String responseRestURL;

    public List<Response> getResponsesByApplicationId(Long applicationId) {
        return asList(restGet(responseRestURL + "/findResponsesByApplication/" + applicationId, Response[].class));
    }

    @Override
    public RestResult<Void> saveQuestionResponseAssessorFeedback(Long assessorUserId, Long responseId, Optional<String> feedbackValue, Optional<String> feedbackText) {

        String url = responseRestURL + "/saveQuestionResponse/" + responseId +
                "/assessorFeedback?assessorUserId=" + assessorUserId +
                "&feedbackValue=" + feedbackValue.orElse("") +
                "&feedbackText=" + feedbackText.orElse("");

        return putWithRestResult(url, Void.class);
    }
}
