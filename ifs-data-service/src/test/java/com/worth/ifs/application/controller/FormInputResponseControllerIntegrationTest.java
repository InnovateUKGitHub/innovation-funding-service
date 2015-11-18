package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseControllerIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class FormInputResponseControllerIntegrationTest extends BaseControllerIntegrationTest<FormInputResponseController> {

    @Override
    @Autowired
    protected void setControllerUnderTest(FormInputResponseController controller) {
        this.controller = controller;
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
