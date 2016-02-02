package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

/**
 * ResponseRestServiceImpl is a utility for CRUD operations on {@link Response}'s.
 * This class connects to the {@link com.worth.ifs.application.controller.ResponseController}
 * through a REST call.
 */
@Service
public class ResponseRestServiceImpl extends BaseRestService implements ResponseRestService {
    @Value("${ifs.data.service.rest.response}")
    String responseRestURL;

    private final Log log = LogFactory.getLog(getClass());


    public List<Response> getResponsesByApplicationId(Long applicationId) {
        return Arrays.asList(restGet(responseRestURL + "/findResponsesByApplication/" + applicationId, Response[].class));
    }

    @Override
    public Boolean saveQuestionResponseAssessorFeedback(Long assessorUserId, Long responseId, Optional<String> feedbackValue, Optional<String> feedbackText) {
        String url = responseRestURL + "/saveQuestionResponse/" + responseId +
                "/assessorFeedback?assessorUserId=" + assessorUserId +
                "&feedbackValue=" + feedbackValue.orElse("") +
                "&feedbackText=" + feedbackText.orElse("");

        RestResult<Void> response = restPutEntity2(url, Void.class, OK);
        return handleRestResult(response);
    }

    // TODO DW - INFUND-854 - should be actually returning RestResponses from this service
    public Boolean handleRestResult(RestResult<?> response) {
        if (response.getStatusCode() == HttpStatus.ACCEPTED || response.getStatusCode() == OK) {
            return true;
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            // nono... bad credentials
            log.info("Unauthorized.....");
            return false;
        }

        return false;
    }

    private Boolean handleResponseStatus(ResponseEntity<?> response) {
        if (response.getStatusCode() == HttpStatus.ACCEPTED || response.getStatusCode() == OK) {
            return true;
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            // nono... bad credentials
            log.info("Unauthorized.....");
            return false;
        }

        return false;
    }
}
