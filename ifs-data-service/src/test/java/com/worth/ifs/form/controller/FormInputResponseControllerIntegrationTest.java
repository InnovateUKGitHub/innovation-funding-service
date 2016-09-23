package com.worth.ifs.form.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.form.resource.FormInputResponseResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION;
import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.security.SecuritySetter.addBasicSecurityUser;
import static java.util.Collections.nCopies;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class FormInputResponseControllerIntegrationTest extends BaseControllerIntegrationTest<FormInputResponseController> {

    @Override
    @Autowired
    protected void setControllerUnderTest(FormInputResponseController controller) {
        this.controller = controller;
    }

    @Before
    public void setup(){
        addBasicSecurityUser();
    }

    @Test
    @Rollback
    public void test_findResponsesByApplication(){
        Long applicationId = 1L;
        Long formInputId = 1L;
        List<FormInputResponseResource> responses = controller.findResponsesByApplication(applicationId).getSuccessObject();

        assertThat(responses, hasSize(16));

        Optional<FormInputResponseResource> response = responses.stream().filter(r -> r.getFormInput().equals(formInputId)).findFirst();

        assertTrue(response.isPresent());
        assertThat(response.get().getValue(), containsString("Within the Industry one issue has caused"));
    }


    @Test
    @Rollback
    public void test_saveNotAllowed() {

        setLoggedInUser(getAnonUser());
        Long applicationId = 1L;
        Long formInputId = 1L;
        String inputValue = "NOT ALLOWED";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObj = mapper.createObjectNode();
        jsonObj.put("userId", Long.MAX_VALUE);
        jsonObj.put("applicationId", applicationId);
        jsonObj.put("formInputId", formInputId);
        jsonObj.put("value", inputValue);

        RestResult<ValidationMessages> result = controller.saveQuestionResponse(jsonObj);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION));
        loginSteveSmith();
        List<FormInputResponseResource> responses = controller.findResponsesByApplication(applicationId).getSuccessObject();
        Optional<FormInputResponseResource> response = responses.stream().filter(r -> r.getFormInput().equals(formInputId)).findFirst();

        assertTrue(response.isPresent());
        assertNotEquals(inputValue, response.get().getValue());
        assertThat(response.get().getValue(), containsString("Within the Industry one issue has caused"));
    }

    @Test
    @Rollback
    public void test_saveInvalidQuestionResponse() {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObj = mapper.createObjectNode();
        jsonObj.put("userId", 1);
        jsonObj.put("applicationId", 1);
        jsonObj.put("formInputId", 1);
        jsonObj.put("value", "");

        ValidationMessages errors = controller.saveQuestionResponse(jsonObj).getSuccessObject();
        assertThat(errors.getErrors(), hasSize(1));
        assertThat(errors.getErrors(), hasItem(fieldError("value", "", "validation.field.please.enter.some.text")));
    }

    @Test
    @Rollback
    public void test_saveWordCountExceedingQuestionResponse() {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObj = mapper.createObjectNode();
        jsonObj.put("userId", 1);
        jsonObj.put("applicationId", 1);
        jsonObj.put("formInputId", 1);

        String value = String.join(" ", nCopies(501, "word"));

        jsonObj.put("value", value);

        ValidationMessages errors = controller.saveQuestionResponse(jsonObj).getSuccessObject();
        assertThat(errors.getErrors(), hasSize(1));
        assertThat(errors.getErrors(), hasItem(fieldError("value", value, "validation.field.max.word.count", "", "400")));
    }

    @Test
    @Rollback
    public void test_saveQuestionResponse() {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObj = mapper.createObjectNode();
        jsonObj.put("userId", 1);
        jsonObj.put("applicationId", 1);
        jsonObj.put("formInputId", 1);
        jsonObj.put("markedAsComplete", 1);
        jsonObj.put("value", "Some text value...");

        ValidationMessages errors = controller.saveQuestionResponse(jsonObj).getSuccessObject();
        assertThat(errors.getErrors(), hasSize(0));
    }
}
