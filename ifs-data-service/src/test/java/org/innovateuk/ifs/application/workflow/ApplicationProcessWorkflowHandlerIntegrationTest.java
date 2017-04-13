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
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.function.Function;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ApplicationProcessWorkflowHandlerIntegrationTest extends BaseWorkflowHandlerIntegrationTest<ApplicationProcessWorkflowHandler, ApplicationProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private ApplicationProcessWorkflowHandler applicationProcessWorkflowHandler;

    private ActivityStateRepository activityStateRepositoryMock;
    private ApplicationProcessRepository applicationProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        applicationProcessRepositoryMock = (ApplicationProcessRepository) mockSupplier.apply(ApplicationProcessRepository.class);
    }

    @Test
    public void testFoo() {

        Application application = newApplication().build();
        ApplicationProcess applicationProcess = new ApplicationProcess(application, null, new ActivityState(ActivityType.APPLICATION, State.CREATED));
        when(applicationProcessRepositoryMock.findOneByTargetId(application.getId())).thenReturn(applicationProcess);

        ActivityState exoectedActivityState = new ActivityState(ActivityType.APPLICATION, ApplicationState.OPEN.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(ActivityType.APPLICATION, ApplicationState.OPEN.getBackingState())).thenReturn(exoectedActivityState);

        boolean result = applicationProcessWorkflowHandler.open(application);

        assertTrue(result);

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
