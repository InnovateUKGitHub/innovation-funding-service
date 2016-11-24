package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.commons.error.CommonErrors.forbiddenError;
import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION;
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
    public void updateFormInputResponse() throws Exception {
        Long assessmentId = 2L;
        Long questionId = 1L;
        Long formInputId = 169L;
        String oldValue = "This is the feedback from Professor Plum for Business opportunity.";
        String newValue = "Feedback";

        loginPaulPlum();
        RestResult<List<AssessorFormInputResponseResource>> allResponsesBeforeResult = controller.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId);
        assertTrue(allResponsesBeforeResult.isSuccess());
        List<AssessorFormInputResponseResource> allResponsesBefore = allResponsesBeforeResult.getSuccessObject();
        Optional<AssessorFormInputResponseResource> toBeUpdated = allResponsesBefore.stream().filter(assessorFormInputResponse -> formInputId.equals(assessorFormInputResponse.getFormInput())).findFirst();
        assertTrue(toBeUpdated.isPresent());

        assertEquals(oldValue, toBeUpdated.get().getValue());

        AssessorFormInputResponseResource updatedAssessorResponse = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(newValue)
                .build();
        RestResult<Void> updateResult = controller.updateFormInputResponse(updatedAssessorResponse);
        assertTrue(updateResult.isSuccess());

        RestResult<List<AssessorFormInputResponseResource>> allResponsesAfterResult = controller.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId);
        assertTrue(allResponsesAfterResult.isSuccess());
        List<AssessorFormInputResponseResource> allResponsesAfter = allResponsesAfterResult.getSuccessObject();
        Optional<AssessorFormInputResponseResource> updated = allResponsesAfter.stream().filter(assessorFormInputResponse -> formInputId.equals(assessorFormInputResponse.getFormInput())).findFirst();
        assertTrue(updated.isPresent());

        assertEquals(newValue, updated.get().getValue());
    }

    @Test
    public void updateFormInputResponse_notTheFormOwner() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 169L;
        String newValue = "Feedback";

        AssessorFormInputResponseResource updatedAssessorResponse = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(newValue)
                .build();

        loginSteveSmith();
        RestResult<Void> updateResult = controller.updateFormInputResponse(updatedAssessorResponse);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }
}