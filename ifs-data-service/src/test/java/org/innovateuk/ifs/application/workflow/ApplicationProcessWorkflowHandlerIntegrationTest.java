package org.innovateuk.ifs.application.workflow;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.repository.ApplicationProcessRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationProcessWorkflowHandler;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.function.Function;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ApplicationProcessWorkflowHandlerIntegrationTest extends BaseWorkflowHandlerIntegrationTest<ApplicationProcessWorkflowHandler, ApplicationProcessRepository, TestableTransitionWorkflowAction> {

    private static final ActivityType activityType = ActivityType.APPLICATION;

    @Autowired
    private ApplicationProcessWorkflowHandler applicationProcessWorkflowHandler;

    private ActivityStateRepository activityStateRepositoryMock;
    private ApplicationProcessRepository applicationProcessRepositoryMock;

    private Application application;


    @Before
    public void setUp() throws Exception {
        application = newApplication().build();
    }

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        applicationProcessRepositoryMock = (ApplicationProcessRepository) mockSupplier.apply(ApplicationProcessRepository.class);
    }

    @Test
    public void open() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.CREATED, ApplicationState.OPEN, application -> applicationProcessWorkflowHandler.open(application));
    }

    @Test
    public void submit() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.OPEN, ApplicationState.SUBMITTED, application -> applicationProcessWorkflowHandler.submit(application));
    }

    @Test
    public void markIneligible() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.SUBMITTED, ApplicationState.INELIGIBLE, application -> applicationProcessWorkflowHandler.markIneligible(application));
    }

    @Test
    public void informIneligible() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.INELIGIBLE, ApplicationState.INELIGIBLE_INFORMED, application -> applicationProcessWorkflowHandler.informIneligible(application));
    }

    @Test
    public void reinstateIneligible() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.INELIGIBLE, ApplicationState.SUBMITTED, application -> applicationProcessWorkflowHandler.reinstateIneligible(application));
    }

    @Test
    public void reinstateIneligible_ineligibleInformed() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.INELIGIBLE_INFORMED, ApplicationState.SUBMITTED, application -> applicationProcessWorkflowHandler.reinstateIneligible(application));
    }

    @Test
    public void approve() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.SUBMITTED, ApplicationState.APPROVED, application -> applicationProcessWorkflowHandler.approve(application));
    }

    @Test
    public void reject() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.SUBMITTED, ApplicationState.REJECTED, application -> applicationProcessWorkflowHandler.reject(application));
    }

    private void assertStateChangeOnWorkflowHandlerCall(ApplicationState initialApplicationState, ApplicationState expectedApplicationState, Function<Application, Boolean> workflowHandlerMethod) {
        ApplicationProcess applicationProcess = new ApplicationProcess(application, null, new ActivityState(activityType, initialApplicationState.getBackingState()));
        when(applicationProcessRepositoryMock.findOneByTargetId(application.getId())).thenReturn(applicationProcess);

        ActivityState expectedActivityState = new ActivityState(activityType, expectedApplicationState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(activityType, expectedApplicationState.getBackingState())).thenReturn(expectedActivityState);

        assertTrue( workflowHandlerMethod.apply(application) );

        assertEquals(expectedApplicationState, applicationProcess.getActivityState());

        verify(applicationProcessRepositoryMock, times(2)).findOneByTargetId(application.getId());
        verify(activityStateRepositoryMock).findOneByActivityTypeAndState(activityType, expectedApplicationState.getBackingState());
        verify(applicationProcessRepositoryMock).save(applicationProcess);
        verifyNoMoreInteractionsWithMocks();
    }

    @Override
    protected Class<TestableTransitionWorkflowAction> getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<ApplicationProcessWorkflowHandler> getWorkflowHandlerType() {
        return ApplicationProcessWorkflowHandler.class;
    }

    @Override
    protected Class<ApplicationProcessRepository> getProcessRepositoryType() {
        return ApplicationProcessRepository.class;
    }
}
