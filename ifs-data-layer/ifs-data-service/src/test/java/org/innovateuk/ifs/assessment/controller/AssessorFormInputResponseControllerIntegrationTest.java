package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponsesResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AssessorFormInputResponseControllerIntegrationTest extends BaseControllerIntegrationTest<AssessorFormInputResponseController> {

    @Autowired
    @Override
    protected void setControllerUnderTest(AssessorFormInputResponseController controller) {
        this.controller = controller;
    }

    @Test
    public void getAllAssessorFormInputResponses() throws Exception {
        Long assessmentId = 2L;

        loginPaulPlum();
        RestResult<List<AssessorFormInputResponseResource>> found = controller.getAllAssessorFormInputResponses(assessmentId);

        assertTrue(found.isSuccess());
        List<AssessorFormInputResponseResource> responses = found.getSuccessObjectOrThrowException();
        assertEquals(13, responses.size());
    }

    @Test
    public void getAllAssessorFormInputResponses_notAssessor() throws Exception {
        Long assessmentId = 1L;

        loginSteveSmith();
        RestResult<List<AssessorFormInputResponseResource>> result = controller.getAllAssessorFormInputResponses(assessmentId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void getAllAssessorFormInputResponsesByAssessmentAndQuestion() throws Exception {
        Long assessmentId = 2L;
        Long questionId = 1L;

        loginPaulPlum();
        RestResult<List<AssessorFormInputResponseResource>> found = controller.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId);

        assertTrue(found.isSuccess());
        List<AssessorFormInputResponseResource> responses = found.getSuccessObjectOrThrowException();
        assertEquals(2, responses.size());
        assertEquals(Long.valueOf(24L), responses.get(0).getId());
        assertEquals(Long.valueOf(25L), responses.get(1).getId());
    }

    @Test
    public void getAllAssessorFormInputResponsesByAssessmentAndQuestion_notAssessor() throws Exception {
        Long assessmentId = 1L;
        Long questionId = 1L;

        loginSteveSmith();
        RestResult<List<AssessorFormInputResponseResource>> result = controller.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void updateFormInputResponses() throws Exception {
        Long assessmentId = 2L;
        Long questionId = 1L;
        Long formInputId = 169L;
        String newValue = "Response";

        loginPaulPlum();

        RestResult<List<AssessorFormInputResponseResource>> allResponsesBeforeResult = controller.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId);
        assertTrue(allResponsesBeforeResult.isSuccess());

        Optional<AssessorFormInputResponseResource> toBeUpdated = allResponsesBeforeResult.getSuccessObject().stream()
                .filter(assessorFormInputResponse -> formInputId.equals(assessorFormInputResponse.getFormInput()))
                .findFirst();
        assertTrue(toBeUpdated.isPresent());
        assertEquals("This is the feedback from Professor Plum for Business opportunity.",
                toBeUpdated.orElseThrow(() -> new IllegalStateException("Expected a response for the form input")).getValue());

        AssessorFormInputResponsesResource updatedAssessorResponses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .withAssessment(assessmentId)
                        .withFormInput(formInputId)
                        .withValue(newValue)
                        .build());

        RestResult<Void> updateResult = controller.updateFormInputResponses(updatedAssessorResponses);
        assertTrue(updateResult.isSuccess());

        RestResult<List<AssessorFormInputResponseResource>> allResponsesAfterResult = controller
                .getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId);
        assertTrue(allResponsesAfterResult.isSuccess());

        Optional<AssessorFormInputResponseResource> updated = allResponsesAfterResult.getSuccessObject().stream()
                .filter(assessorFormInputResponse -> formInputId.equals(assessorFormInputResponse.getFormInput())).findFirst();

        assertTrue(updated.isPresent());

        assertEquals(newValue, updated.orElseThrow(() ->
                new IllegalStateException("Expected a response for the form input")).getValue());
    }

    @Test
    public void updateFormInputResponses_notTheFormOwner() throws Exception {
        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .withAssessment(1L)
                        .withFormInput(169L)
                        .withValue("Response")
                        .build());

        loginSteveSmith();
        RestResult<Void> updateResult = controller.updateFormInputResponses(responses);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }
}
