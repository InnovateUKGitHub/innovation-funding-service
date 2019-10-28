package org.innovateuk.ifs.form.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.controller.FormInputResponseController;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.nCopies;
import static org.hamcrest.Matchers.*;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.security.SecuritySetter.addBasicSecurityUser;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.PROJECT_SUMMARY;
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
    public void test_findResponsesByApplication() {
        Long applicationId = 1L;
        Long formInputId = 1L;
        List<FormInputResponseResource> responses = controller.findResponsesByApplication(applicationId).getSuccess();

        assertThat(responses, hasSize(15));

        Optional<FormInputResponseResource> response = responses.stream().filter(r -> r.getFormInput().equals(formInputId)).findFirst();

        assertTrue(response.isPresent());
        assertThat(response.get().getValue(), containsString("Within the Industry one issue has caused"));
    }

    @Test
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
        List<FormInputResponseResource> responses = controller.findResponsesByApplication(applicationId).getSuccess();
        Optional<FormInputResponseResource> response = responses.stream().filter(r -> r.getFormInput().equals(formInputId)).findFirst();

        assertTrue(response.isPresent());
        assertNotEquals(inputValue, response.get().getValue());
        assertThat(response.get().getValue(), containsString("Within the Industry one issue has caused"));
    }

    @Test
    public void test_saveInvalidQuestionResponse() {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObj = mapper.createObjectNode();
        jsonObj.put("userId", 1);
        jsonObj.put("applicationId", 1);
        jsonObj.put("formInputId", 1);
        jsonObj.put("value", "");

        ValidationMessages errors = controller.saveQuestionResponse(jsonObj).getSuccess();
        assertThat(errors.getErrors(), hasSize(1));
        assertThat(errors.getErrors(), hasItem(fieldError("value", "", "validation.field.please.enter.some.text")));
    }

    @Test
    public void test_saveWordCountExceedingQuestionResponse() {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObj = mapper.createObjectNode();
        jsonObj.put("userId", 1);
        jsonObj.put("applicationId", 1);
        jsonObj.put("formInputId", 1);

        String value = String.join(" ", nCopies(501, "word"));

        jsonObj.put("value", value);

        ValidationMessages errors = controller.saveQuestionResponse(jsonObj).getSuccess();
        assertThat(errors.getErrors(), hasSize(1));
        assertThat(errors.getErrors(), hasItem(fieldError("value", value, "validation.field.max.word.count", "1", "400")));
    }

    @Test
    public void test_saveQuestionResponse() {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonObj = mapper.createObjectNode();
        jsonObj.put("userId", 1);
        jsonObj.put("applicationId", 1);
        jsonObj.put("formInputId", 1);
        jsonObj.put("markedAsComplete", 1);
        jsonObj.put("value", "Some text value...");

        ValidationMessages errors = controller.saveQuestionResponse(jsonObj).getSuccess();
        assertThat(errors.getErrors(), hasSize(0));
    }

    @Test
    public void findByApplicationIdAndQuestionSetupType() {
        long applicationId = 1L;
        QuestionSetupType questionSetupType = PROJECT_SUMMARY;

        FormInputResponseResource formInputResponseResource = controller.findByApplicationIdAndQuestionSetupType(applicationId,
                questionSetupType).getSuccess();

        assertEquals(15L, formInputResponseResource.getId().longValue());
        assertTrue(formInputResponseResource.getValue().startsWith("The Project aims to identify,"));
    }

    @Test
    public void findByApplicationIdAndQuestionId() {
        long applicationId = 1L;
        long questionId = 1L;

        List<FormInputResponseResource> formInputResponseResource = controller.findByApplicationIdAndQuestionId(applicationId, questionId).getSuccess();
        assertEquals(1, formInputResponseResource.size());
        assertEquals(1L, (long)formInputResponseResource.get(0).getId());
        assertTrue(formInputResponseResource.get(0).getValue().startsWith("Within the Industry one issue has caused progress"));
    }
}
