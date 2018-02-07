package org.innovateuk.ifs.assessment.interview.workflow;

import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterview;
import org.innovateuk.ifs.assessment.interview.repository.AssessmentInterviewRepository;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState;
import org.innovateuk.ifs.assessment.interview.workflow.configuration.AssessmentInterviewWorkflowHandler;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.innovateuk.ifs.assessment.interview.builder.AssessmentInterviewBuilder.newAssessmentInterview;
import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState.CREATED;
import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState.PENDING;
import static org.innovateuk.ifs.workflow.domain.ActivityType.ASSESSMENT_INTERVIEW;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
public class AssessmentInterviewWorkflowHandlerIntegrationTest
        extends BaseWorkflowHandlerIntegrationTest<
        AssessmentInterviewWorkflowHandler,
        AssessmentInterviewRepository, TestableTransitionWorkflowAction> {

    private static final ActivityType ACTIVITY_TYPE = ASSESSMENT_INTERVIEW;

    @Autowired
    private AssessmentInterviewWorkflowHandler workflowHandler;

    private ActivityStateRepository activityStateRepositoryMock;
    private AssessmentInterviewRepository repositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        repositoryMock = (AssessmentInterviewRepository) mockSupplier.apply(AssessmentInterviewRepository.class);
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(ProcessRoleRepository.class);
        return repositories;
    }

    @Test
    public void notifyInvitation() {
        assertStateChangeOnWorkflowHandlerCall(CREATED, PENDING, invite -> workflowHandler.notifyInvitation(invite));
    }

    @Override
    protected Class<TestableTransitionWorkflowAction> getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<AssessmentInterviewWorkflowHandler> getWorkflowHandlerType() {
        return AssessmentInterviewWorkflowHandler.class;
    }

    @Override
    protected Class<AssessmentInterviewRepository> getProcessRepositoryType() {
        return AssessmentInterviewRepository.class;
    }

    private ActivityType getActivityType() {
        return ACTIVITY_TYPE;
    }

    private AssessmentInterviewRepository getRepositoryMock() {
        return repositoryMock;
    }


    private AssessmentInterview buildWorkflowProcessWithInitialState(AssessmentInterviewState initialState) {
        return newAssessmentInterview().withState(initialState).build();
    }

    private void assertStateChangeOnWorkflowHandlerCall(AssessmentInterviewState initialState, AssessmentInterviewState expectedState, Function<AssessmentInterview, Boolean> workflowHandlerMethod) {
        assertStateChangeOnWorkflowHandlerCall(initialState, expectedState, workflowHandlerMethod, null);
    }

    private void assertStateChangeOnWorkflowHandlerCall(AssessmentInterviewState initialState, AssessmentInterviewState expectedState, Function<AssessmentInterview, Boolean> workflowHandlerMethod, Consumer<AssessmentInterview> additionalVerifications) {
        AssessmentInterview workflowProcess = buildWorkflowProcessWithInitialState(initialState);
        when(getRepositoryMock().findOneByTargetId(workflowProcess.getId())).thenReturn(workflowProcess);

        ActivityState expectedActivityState = new ActivityState(getActivityType(), expectedState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(getActivityType(), expectedState.getBackingState())).thenReturn(expectedActivityState);

        assertTrue(workflowHandlerMethod.apply(workflowProcess));

        assertEquals(expectedState, workflowProcess.getActivityState());

        verify(activityStateRepositoryMock).findOneByActivityTypeAndState(getActivityType(), expectedState.getBackingState());
        verify(getRepositoryMock()).save(workflowProcess);

        if (additionalVerifications != null) {
            additionalVerifications.accept(workflowProcess);
        }

        verifyNoMoreInteractionsWithMocks();
    }
}