package com.worth.ifs.form.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.ParameterizedTypeReferences;
import com.worth.ifs.form.resource.FormInputResponseResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.formInputResponseListType;
import static com.worth.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
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
        List<FormInputResponseResource> returnedResponses = FormInputResponseResourceBuilder.newFormInputResponseResource().build(3);

        setupGetWithRestResultExpectations(formInputResponseRestURL + "/findResponsesByApplication/123", ParameterizedTypeReferences.formInputResponseListType(), returnedResponses);

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

        List<Error> returnedResponses = asList(new Error("A returned string", BAD_REQUEST), new Error("A returned string 2", BAD_REQUEST));
        ValidationMessages validationMessages = new ValidationMessages(returnedResponses);

        setupPostWithRestResultExpectations(formInputResponseRestURL + "/saveQuestionResponse/", ValidationMessages.class, entityUpdates, validationMessages, OK);

        ValidationMessages responses = service.saveQuestionResponse(123L, 456L, 789L, "Very good answer!", false).getSuccessObject();
        Assert.assertEquals(returnedResponses, responses.getErrors());
    }

    @Test
    public void test_getByFormInputIdAndApplication(){
        List<FormInputResponseResource> returnedResponses = FormInputResponseResourceBuilder.newFormInputResponseResource().build(3);

        setupGetWithRestResultExpectations(formInputResponseRestURL + "/findResponseByFormInputIdAndApplicationId/456/123", ParameterizedTypeReferences.formInputResponseListType(), returnedResponses);

        List<FormInputResponseResource> responses = service.getByFormInputIdAndApplication(456L, 123L).getSuccessObject();
        assertEquals(returnedResponses, responses);
    }
}
