package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.util.Either;
import org.junit.Test;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.ResponseBuilder.newResponse;
import static com.worth.ifs.assessment.transactional.AssessorServiceImpl.ServiceFailures.*;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
import static com.worth.ifs.user.domain.UserRoleType.COLLABORATOR;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link AssessorServiceImpl}
 * <p>
 * Created by dwatson on 07/10/15.
 */
public class AssessorServiceImplMockTest extends BaseServiceUnitTest<AssessorService> {

    @Override
    protected AssessorService supplyServiceUnderTest() {
        return new AssessorServiceImpl();
    }

    @Test
    public void test_responseNotFound() {

        long responseId = 1L;
        when(responseRepositoryMock.findOne(responseId)).thenReturn(null);
        Either<ServiceFailure, Feedback> serviceResult
                = service.updateAssessorFeedback(
                new Feedback()
                        .setResponseId(responseId)
                        .setAssessorProcessRoleId(2L)
                        .setValue(empty())
                        .setText(empty()));
        assertTrue(serviceResult.isLeft());
        assertTrue(serviceResult.getLeft().is(RESPONSE_NOT_FOUND));
    }

    @Test
    public void test_processRoleNotFound() {

        long responseId = 1L;
        long processRoleId = 2L;

        when(responseRepositoryMock.findOne(responseId)).thenReturn(newResponse().build());
        when(processRoleRepositoryMock.findOne(processRoleId)).thenReturn(null);

        Either<ServiceFailure, Feedback> serviceResult
                = service.updateAssessorFeedback(
                new Feedback()
                        .setResponseId(responseId)
                        .setAssessorProcessRoleId(processRoleId)
                        .setValue(empty())
                        .setText(empty()));
        assertTrue(serviceResult.isLeft());
        assertTrue(serviceResult.getLeft().is(PROCESS_ROLE_NOT_FOUND));
    }

    @Test
    public void test_processRoleNotCorrectType() {

        long responseId = 1L;
        long processRoleId = 2L;

        ProcessRole incorrectTypeProcessRole = newProcessRole().
                withRole(newRole().withType(COLLABORATOR)).
                build();

        when(responseRepositoryMock.findOne(responseId)).thenReturn(newResponse().build());
        when(processRoleRepositoryMock.findOne(processRoleId)).thenReturn(incorrectTypeProcessRole);

        Either<ServiceFailure, Feedback> serviceResult
                = service.updateAssessorFeedback(
                new Feedback()
                        .setResponseId(responseId)
                        .setAssessorProcessRoleId(processRoleId)
                        .setValue(empty())
                        .setText(empty()));
        assertTrue(serviceResult.isLeft());
        assertTrue(serviceResult.getLeft().is(PROCESS_ROLE_INCORRECT_TYPE));
    }

    @Test
    public void test_processRoleNotCorrectApplication() {

        long responseId = 1L;
        long processRoleId = 2L;
        long correctApplicationId = 3L;
        long incorrectApplicationId = -999L;

        ProcessRole incorrectApplicationProcessRole =
                newProcessRole().
                        withRole(newRole().withType(ASSESSOR)).
                        withApplication(newApplication().withId(incorrectApplicationId)).
                        build();

        Response response =
                newResponse().
                        withApplication(newApplication().withId(correctApplicationId)).
                        build();

        when(responseRepositoryMock.findOne(responseId)).thenReturn(response);
        when(processRoleRepositoryMock.findOne(processRoleId)).thenReturn(incorrectApplicationProcessRole);

        Either<ServiceFailure, Feedback> serviceResult
                = service.updateAssessorFeedback(
                new Feedback()
                        .setResponseId(responseId)
                        .setAssessorProcessRoleId(processRoleId)
                        .setValue(empty())
                        .setText(empty()));
        assertTrue(serviceResult.isLeft());
        assertTrue(serviceResult.getLeft().is(PROCESS_ROLE_INCORRECT_APPLICATION));
    }

    @Test
    public void test_uncaughtExceptions_handled() {

        long responseId = 1L;
        when(responseRepositoryMock.findOne(responseId)).thenThrow(new RuntimeException());
        Either<ServiceFailure, Feedback> serviceResult
                = service.updateAssessorFeedback(
                new Feedback()
                        .setResponseId(responseId)
                        .setAssessorProcessRoleId(2L)
                        .setValue(empty())
                        .setText(empty()));
        assertTrue(serviceResult.isLeft());
        assertTrue(serviceResult.getLeft().is(UNEXPECTED_ERROR));
    }

    @Test
    public void test_happyPath_assessmentFeedbackUpdated() {

        long responseId = 1L;
        long processRoleId = 2L;
        long applicationId = 3L;

        Application application =
                newApplication().
                        withId(applicationId).
                        build();

        ProcessRole processRole =
                newProcessRole().
                        withId(processRoleId).
                        withRole(newRole().withType(ASSESSOR)).
                        withApplication(application).
                        build();

        Response response =
                newResponse().
                        withApplication(application).
                        build();

        when(responseRepositoryMock.findOne(responseId)).thenReturn(response);
        when(processRoleRepositoryMock.findOne(processRoleId)).thenReturn(processRole);
        when(responseRepositoryMock.save(response)).thenReturn(response);

        Either<ServiceFailure, Feedback> serviceResult
                = service.updateAssessorFeedback(
                new Feedback()
                        .setResponseId(responseId)
                        .setAssessorProcessRoleId(processRoleId)
                        .setValue(of("newFeedbackValue"))
                        .setText(of("newFeedbackText")));
        assertTrue(serviceResult.isRight());

        AssessorFeedback feedback = response.getResponseAssessmentForAssessor(processRole).orElse(null);

        assertNotNull(feedback);
        assertEquals("newFeedbackValue", feedback.getAssessmentValue());
        assertEquals("newFeedbackText", feedback.getAssessmentFeedback());
    }

}
