package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentStates;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.assessment.dto.Score;
import com.worth.ifs.assessment.workflow.AssessmentWorkflowEventHandler;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.builder.ProcessRoleResourceBuilder;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.transactional.UsersRolesService;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.ResponseBuilder.newResponse;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

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

    @Mock
    private AssessmentWorkflowEventHandler assessmentWorkflowEventHandlerMock;

    @Mock
    private UsersRolesService usersRolesServiceMock;

    @Test
    public void test_responseNotFound() {

        long responseId = 123L;
        when(responseRepositoryMock.findOne(responseId)).thenReturn(null);

        ServiceResult<Feedback> serviceResult = service.updateAssessorFeedback(
                new Feedback.Id(responseId, 2L), empty(), empty());

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(Response.class, responseId)));
    }

    @Test
    public void test_roleNotFound() {

        when(responseRepositoryMock.findOne(123L)).thenReturn(newResponse().build());
        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(emptyList());

        ServiceResult<Feedback> serviceResult = service.updateAssessorFeedback(
                new Feedback.Id(123L, 2L), empty(), empty());

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(Role.class, ASSESSOR.getName())));
    }

    @Test
    public void test_processRole_notFound() {

        long responseId = 1L;
        long applicationId = 3L;
        long userId = 4L;

        Role assessorRole = newRole().withType(ASSESSOR).build();

        Application application =
                newApplication().
                        withId(applicationId).
                        build();

        Response response =
                newResponse().
                        withApplication(application).
                        build();

        when(responseRepositoryMock.findOne(responseId)).thenReturn(response);
        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(singletonList(assessorRole));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(userId, assessorRole, applicationId)).thenReturn(emptyList());

        ServiceResult<Feedback> serviceResult = service.updateAssessorFeedback(
                new Feedback.Id(responseId, userId), of("newFeedbackValue"), of("newFeedbackText"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(ProcessRole.class, userId, ASSESSOR.getName(), applicationId)));
    }

    @Test
    public void test_happyPath_assessmentFeedbackUpdated() {

        long responseId = 1L;
        long processRoleId = 2L;
        long applicationId = 3L;
        long userId = 4L;

        Role assessorRole = newRole().build();

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
        when(roleRepositoryMock.findByName(ASSESSOR.getName())).thenReturn(singletonList(assessorRole));
        when(processRoleRepositoryMock.findByUserIdAndRoleAndApplicationId(userId, assessorRole, applicationId)).thenReturn(singletonList(processRole));
        when(responseRepositoryMock.save(response)).thenReturn(response);

        ServiceResult<Feedback> serviceResult = service.updateAssessorFeedback(
                new Feedback.Id(responseId, userId), of("newFeedbackValue"), of("newFeedbackText"));

        assertTrue(serviceResult.isSuccess());

        AssessorFeedback feedback = response.getResponseAssessmentForAssessor(processRole).orElse(null);

        assertNotNull(feedback);
        assertEquals("newFeedbackValue", feedback.getAssessmentValue());
        assertEquals("newFeedbackText", feedback.getAssessmentFeedback());
    }

    @Test
    public void testFindByProcessRole() {

        Assessment assessment = newAssessment().build();

        when(assessmentRepositoryMock.findOneByProcessRoleId(123L)).thenReturn(assessment);

        ServiceResult<Assessment> result = service.getOneByProcessRole(123L);
        assertTrue(result.isSuccess());
        assertEquals(assessment, result.getSuccessObject());
    }

    @Test
    public void testFindByCompetitionAndAssessor() {

        Assessment assessment = newAssessment().build();
        Set<String> states = AssessmentStates.getStates();
        states.remove(AssessmentStates.REJECTED.getState());

        when(assessmentRepositoryMock.findByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatusIn(456L, 123L, states)).thenReturn(singletonList(assessment));

        ServiceResult<List<Assessment>> result = service.getAllByCompetitionAndAssessor(123L, 456L);
        assertTrue(result.isSuccess());
        assertEquals(singletonList(assessment), result.getSuccessObject());
    }

    @Test
    public void testGetScore() {

        Competition competititon = newCompetition().build();
        Application application = newApplication().withCompetition(competititon).build();
        ProcessRole processRole = newProcessRole().withApplication(application).build();
        Assessment assessment = newAssessment().withProcessRole(processRole).build();

        when(assessmentRepositoryMock.findById(123L)).thenReturn(assessment);
        when(responseService.findResponsesByApplication(application.getId())).thenReturn(serviceSuccess(newResponse().build(2)));

        ServiceResult<Score> result = service.getScore(123L);

        assertTrue(result.isSuccess());
        assertEquals(new Score(), result.getSuccessObject());
    }

    @Test
    public void testGetTotalAssignedAssessmentsByCompetition() {

        when(assessmentRepositoryMock.countByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatusNot(456L, 123L, ApplicationStatusConstants.REJECTED.getName())).thenReturn(5);

        ServiceResult<Integer> result = service.getTotalAssignedAssessmentsByCompetition(123L, 456L);
        assertTrue(result.isSuccess());
        assertEquals(Integer.valueOf(5), result.getSuccessObject());
    }

    @Test
    public void testGetTotalSubmittedAssessmentsByCompetition() {

        when(assessmentRepositoryMock.countByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatus(456L, 123L, ApplicationStatusConstants.SUBMITTED.getName())).thenReturn(5);

        ServiceResult<Integer> result = service.getTotalSubmittedAssessmentsByCompetition(123L, 456L);
        assertTrue(result.isSuccess());
        assertEquals(Integer.valueOf(5), result.getSuccessObject());
    }

    @Test
    public void testAcceptAssessmentInvitation() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).build();
        ProcessRole processRole = newProcessRole().withApplication(application).build();
        Assessment assessment = newAssessment().withProcessRole(processRole).build();

        when(assessmentRepositoryMock.findOneByProcessRoleId(123L)).thenReturn(assessment);

        ServiceResult<Void> result = service.acceptAssessmentInvitation(123L, assessment);
        assertTrue(result.isSuccess());

        verify(assessmentWorkflowEventHandlerMock).acceptInvitation(123L, assessment);
    }

    @Test
    public void testRejectAssessmentInvitation() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).build();
        ProcessRole processRole = newProcessRole().withApplication(application).build();
        Assessment assessment = newAssessment().withProcessRole(processRole).build();

        when(assessmentRepositoryMock.findOneByProcessRoleId(123L)).thenReturn(assessment);

        ProcessOutcome processOutcome = newProcessOutcome().build();
        ServiceResult<Void> result = service.rejectAssessmentInvitation(123L, processOutcome);
        assertTrue(result.isSuccess());

        verify(assessmentWorkflowEventHandlerMock).rejectInvitation(123L, null, processOutcome);
    }

    @Test
    public void testSubmitAssessments() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).build();
        ProcessRole processRole = newProcessRole().withApplication(application).build();
        Assessment assessment1 = newAssessment().withProcessRole(processRole).build();
        Assessment assessment2 = newAssessment().withProcessRole(processRole).build();

        when(assessmentRepositoryMock.findById(assessment1.getId())).thenReturn(assessment1);
        when(assessmentRepositoryMock.findById(assessment2.getId())).thenReturn(assessment2);

        ServiceResult<Void> result = service.submitAssessments(newHashSet(assessment1.getId(), assessment2.getId()));
        assertTrue(result.isSuccess());

        verify(assessmentWorkflowEventHandlerMock, times(2)).submit(isA(Assessment.class));
    }

    @Test
    public void testSubmitAssessment() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).build();
        ProcessRole processRole = newProcessRole().withApplication(application).build();
        ProcessRoleResource processRoleResource = newProcessRoleResource().with(id(processRole.getId())).withApplicationId(application.getId()).build();
        Assessment assessment = newAssessment().withProcessRole(processRole).build();

        when(usersRolesServiceMock.getProcessRoleByUserIdAndApplicationId(123L, 456L)).thenReturn(serviceSuccess(processRoleResource));

        when(assessmentRepositoryMock.findOneByProcessRoleId(processRole.getId())).thenReturn(assessment);
        ServiceResult<Void> result = service.submitAssessment(123L, 456L, "a suitable value", "suitable feedback", "some comments");
        assertTrue(result.isSuccess());

        verify(assessmentWorkflowEventHandlerMock).recommend(eq(processRole.getId()), isA(Assessment.class), isA(ProcessOutcome.class));
    }
}
