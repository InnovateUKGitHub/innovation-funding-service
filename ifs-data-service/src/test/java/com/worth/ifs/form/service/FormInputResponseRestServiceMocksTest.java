package com.worth.ifs.form.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResponseResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.formInputResponseListType;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.stringsListType;
import static com.worth.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static com.worth.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
public class FormInputResponseRestServiceMocksTest extends BaseRestServiceUnitTest<FormInputResponseRestServiceImpl> {

    private static final String formInputResponseRestURL = "/forminputresponse";

    @Override
    protected FormInputResponseRestServiceImpl registerRestServiceUnderTest() {
        FormInputResponseRestServiceImpl formInputResponseService = new FormInputResponseRestServiceImpl();
        return formInputResponseService;
    }

    @Test
    public void test_getResponsesByApplicationId() {
        List<FormInputResponseResource> returnedResponses = newFormInputResponseResource().build(3);

        setupGetWithRestResultExpectations(formInputResponseRestURL + "/findResponsesByApplication/123", formInputResponseListType(), returnedResponses);

        List<FormInputResponseResource> responses = service.getResponsesByApplicationId(123L).getSuccessObject();
        assertEquals(returnedResponses, responses);
    }

    @Test
    public void test_saveQuestionResponse() {

        ObjectNode entityUpdates = new ObjectMapper().createObjectNode().
                put("userId", 123L).put("applicationId", 456L).
                put("formInputId", 789L).
                put("value", "Very good answer!").
                put("ignoreEmpty", false);

        List<String> returnedResponses = asList("A returned string", "A returned string 2");

        setupPostWithRestResultExpectations(formInputResponseRestURL + "/saveQuestionResponse/", stringsListType(), entityUpdates, returnedResponses, OK);

        List<String> responses = service.saveQuestionResponse(123L, 456L, 789L, "Very good answer!", false).getSuccessObject();
        assertEquals(returnedResponses, responses);
    }

    @Test
    public void test_getByFormInputIdAndApplication(){
        List<FormInputResponseResource> returnedResponses = newFormInputResponseResource().build(3);

        setupGetWithRestResultExpectations(formInputResponseRestURL + "/findResponseByFormInputIdAndApplicationId/456/123", formInputResponseListType(), returnedResponses);

        List<FormInputResponseResource> responses = service.getByFormInputIdAndApplication(456L, 123L).getSuccessObject();
        assertEquals(returnedResponses, responses);
    }
}
