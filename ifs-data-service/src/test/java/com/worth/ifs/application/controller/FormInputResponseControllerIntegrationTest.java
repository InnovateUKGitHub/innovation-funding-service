package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.form.domain.FormInputResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class FormInputResponseControllerIntegrationTest extends BaseControllerIntegrationTest<FormInputResponseController> {

    @Override
    @Autowired
    protected void setControllerUnderTest(FormInputResponseController controller) {
        this.controller = controller;
    }

    @Test
    @Rollback
    public void test_findResponsesByApplication(){
        Long applicationId = 1L;
        Long formInputId = 1L;
        List<FormInputResponse> responses = controller.findResponsesByApplication(applicationId);

        assertThat(responses, hasSize(16));

        Optional<FormInputResponse> response = responses.stream().filter(r -> r.getFormInput().getId().equals(formInputId)).findFirst();

        assertTrue(response.isPresent());
        assertThat(response.get().getValue(), containsString("Within the Industry one issue has caused"));
    }


    @Test
    @Rollback
    public void test_saveNotAllowed() {
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        Long applicationId = 1L;
        Long formInputId = 1L;
        String inputValue = "NOT ALLOWED";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObj = mapper.createObjectNode();
        jsonObj.put("userId", Long.MAX_VALUE);
        jsonObj.put("applicationId", applicationId);
        jsonObj.put("formInputId", formInputId);
        jsonObj.put("value", inputValue);

        List<String> errors = controller.saveQuestionResponse(jsonObj, servletResponse);
        assertEquals(null, errors);

        List<FormInputResponse> responses = controller.findResponsesByApplication(applicationId);
        Optional<FormInputResponse> response = responses.stream().filter(r -> r.getFormInput().getId().equals(formInputId)).findFirst();

        assertTrue(response.isPresent());
        assertNotEquals(inputValue, response.get().getValue());
        assertThat(response.get().getValue(), containsString("Within the Industry one issue has caused"));
    }

    @Test
    @Rollback
    public void test_saveInvalidQuestionResponse() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObj = mapper.createObjectNode();
        jsonObj.put("userId", 1);
        jsonObj.put("applicationId", 1);
        jsonObj.put("formInputId", 1);
        jsonObj.put("value", "");

        List<String> errors = controller.saveQuestionResponse(jsonObj, response);
        assertThat(errors, hasSize(1));
        assertThat(errors, hasItem("Please enter some text"));
    }

    @Test
    @Rollback
    public void test_saveQuestionResponse() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObj = mapper.createObjectNode();
        jsonObj.put("userId", 1);
        jsonObj.put("applicationId", 1);
        jsonObj.put("formInputId", 1);
        jsonObj.put("value", "Some text value...");

        List<String> errors = controller.saveQuestionResponse(jsonObj, response);
        assertThat(errors, hasSize(0));
    }
}
