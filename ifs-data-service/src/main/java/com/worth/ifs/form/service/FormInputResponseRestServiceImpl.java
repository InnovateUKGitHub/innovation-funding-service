package com.worth.ifs.form.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.List;

/**
 * ResponseRestServiceImpl is a utility for CRUD operations on {@link Response}'s.
 * This class connects to the {@link com.worth.ifs.application.controller.ResponseController}
 * through a REST call.
 */
@Service
public class FormInputResponseRestServiceImpl extends BaseRestService implements FormInputResponseRestService {
    @Value("${ifs.data.service.rest.forminputresponse}")
    String formInputResponseRestURL;

    private final Log log = LogFactory.getLog(getClass());


    public List<FormInputResponse> getResponsesByApplicationId(Long applicationId) {
        List<FormInputResponse> responses = Arrays.asList(restGet(formInputResponseRestURL + "/findResponsesByApplication/" + applicationId, FormInputResponse[].class));
        return responses;
    }

    public List<String> saveQuestionResponse(Long userId, Long applicationId, Long formInputId, String value) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("userId", userId);
        node.put("applicationId", applicationId);
        node.put("formInputId", formInputId);
        node.put("value", HtmlUtils.htmlEscape(value));
        List<String> validatedResponse = Arrays.asList(restPost(formInputResponseRestURL + "/saveQuestionResponse/", node, String[].class));
        return validatedResponse;
    }

}
