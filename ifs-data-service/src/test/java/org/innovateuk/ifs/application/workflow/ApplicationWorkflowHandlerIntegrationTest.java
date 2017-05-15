package org.innovateuk.ifs.application.workflow;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.repository.ApplicationProcessRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.IneligibleOutcomeBuilder.newIneligibleOutcome;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ApplicationWorkflowHandlerIntegrationTest extends BaseWorkflowHandlerIntegrationTest<ApplicationWorkflowHandler, ApplicationProcessRepository, TestableTransitionWorkflowAction> {

    private static final ActivityType activityType = ActivityType.APPLICATION;

    @Autowired
    private ApplicationWorkflowHandler applicationWorkflowHandler;

    private ActivityStateRepository activityStateRepositoryMock;
    private ApplicationProcessRepository applicationProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        applicationProcessRepositoryMock = (ApplicationProcessRepository) mockSupplier.apply(ApplicationProcessRepository.class);
    }

    @Test
    public void open() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.CREATED, ApplicationState.OPEN, application -> applicationWorkflowHandler.open(application));
    }

    @Test
    public void submit() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.OPEN, ApplicationState.SUBMITTED, application -> applicationWorkflowHandler.submit(application));
    }

    @Test
    public void markIneligible() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.SUBMITTED, ApplicationState.INELIGIBLE, application -> applicationWorkflowHandler.markIneligible(application, createIneligible()), (applicationProcess -> {
            assertEquals(1, applicationProcess.getIneligibleOutcomes().size());
            assertEquals("reason", applicationProcess.getIneligibleOutcomes().get(0).getReason());
        }));
    }

    @Test
    public void informIneligible() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.INELIGIBLE, ApplicationState.INELIGIBLE_INFORMED, application -> applicationWorkflowHandler.informIneligible(application));
    }

    @Test
    public void reinstateIneligible() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.INELIGIBLE, ApplicationState.SUBMITTED, application -> applicationWorkflowHandler.reinstateIneligible(application));
    }

    @Test
    public void reinstateIneligible_ineligibleInformed() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.INELIGIBLE_INFORMED, ApplicationState.SUBMITTED, application -> applicationWorkflowHandler.reinstateIneligible(application));
    }

    @Test
    public void approve() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.SUBMITTED, ApplicationState.APPROVED, application -> applicationWorkflowHandler.approve(application));
    }

    @Test
    public void reject() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.SUBMITTED, ApplicationState.REJECTED, application -> applicationWorkflowHandler.reject(application));
    }

    @Test
    public void notifyFromApplicationState_ineligibleToSubmitted() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.INELIGIBLE, ApplicationState.SUBMITTED, application ->
                applicationWorkflowHandler.notifyFromApplicationState(application, ApplicationState.SUBMITTED));
    }

    @Test
    public void notifyFromApplicationState_ineligibleInformedToSubmitted() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.INELIGIBLE_INFORMED, ApplicationState.SUBMITTED, application ->
                applicationWorkflowHandler.notifyFromApplicationState(application, ApplicationState.SUBMITTED));
    }

    private void assertStateChangeOnWorkflowHandlerCall(ApplicationState initialApplicationState, ApplicationState expectedApplicationState, Function<Application, Boolean> workflowHandlerMethod) {
        assertStateChangeOnWorkflowHandlerCall(initialApplicationState, expectedApplicationState, workflowHandlerMethod, null);
    }

    private void assertStateChangeOnWorkflowHandlerCall(ApplicationState initialApplicationState, ApplicationState expectedApplicationState, Function<Application, Boolean> workflowHandlerMethod, Consumer<ApplicationProcess> additionalVerifications) {
        Application application = newApplication().withApplicationState(initialApplicationState).build();
        ApplicationProcess applicationProcess = application.getApplicationProcess();
        when(applicationProcessRepositoryMock.findOneByTargetId(application.getId())).thenReturn(applicationProcess);

        ActivityState expectedActivityState = new ActivityState(activityType, expectedApplicationState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(activityType, expectedApplicationState.getBackingState())).thenReturn(expectedActivityState);

        assertTrue(workflowHandlerMethod.apply(application));

        assertEquals(expectedApplicationState, applicationProcess.getActivityState());

        verify(applicationProcessRepositoryMock, times(2)).findOneByTargetId(application.getId());
        verify(activityStateRepositoryMock).findOneByActivityTypeAndState(activityType, expectedApplicationState.getBackingState());
        verify(applicationProcessRepositoryMock).save(applicationProcess);

        if (additionalVerifications != null) {
            additionalVerifications.accept(applicationProcess);
        }

        verifyNoMoreInteractionsWithMocks();
    }

    private IneligibleOutcome createIneligible() {
        return newIneligibleOutcome().withReason("reason").build();
    }

    @Override
    protected Class<TestableTransitionWorkflowAction> getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<ApplicationWorkflowHandler> getWorkflowHandlerType() {
        return ApplicationWorkflowHandler.class;
    }

    @Override
    protected Class<ApplicationProcessRepository> getProcessRepositoryType() {
        return ApplicationProcessRepository.class;
    }
}
