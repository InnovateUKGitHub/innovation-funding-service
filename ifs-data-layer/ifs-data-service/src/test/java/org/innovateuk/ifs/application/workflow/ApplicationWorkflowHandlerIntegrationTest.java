package org.innovateuk.ifs.application.workflow;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.repository.ApplicationProcessRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.IneligibleOutcomeBuilder.newIneligibleOutcome;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ApplicationWorkflowHandlerIntegrationTest extends BaseWorkflowHandlerIntegrationTest<ApplicationWorkflowHandler, ApplicationProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private ApplicationWorkflowHandler applicationWorkflowHandler;

    private ApplicationProcessRepository applicationProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        applicationProcessRepositoryMock = (ApplicationProcessRepository) mockSupplier.apply(ApplicationProcessRepository.class);
    }

    @Test
    public void open() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.CREATED, ApplicationState.OPEN, applicationWorkflowHandler::open);
    }

    @Test
    public void submit() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.OPEN, ApplicationState.SUBMITTED, applicationWorkflowHandler::submit);
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
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.INELIGIBLE, ApplicationState.INELIGIBLE_INFORMED, applicationWorkflowHandler::informIneligible);
    }

    @Test
    public void reinstateIneligible() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.INELIGIBLE, ApplicationState.SUBMITTED, applicationWorkflowHandler::reinstateIneligible);
    }

    @Test
    public void reinstateIneligible_ineligibleInformed() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.INELIGIBLE_INFORMED, ApplicationState.SUBMITTED, applicationWorkflowHandler::reinstateIneligible);
    }

    @Test
    public void approve() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.SUBMITTED, ApplicationState.APPROVED, applicationWorkflowHandler::approve);
    }

    @Test
    public void reject() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.SUBMITTED, ApplicationState.REJECTED, applicationWorkflowHandler::reject);
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

    @Test
    public void approveFromApplicationState_rejectedToApproved() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.REJECTED, ApplicationState.APPROVED, applicationWorkflowHandler::approve);
    }

    @Test
    public void withdraw() {
        User internalUser = newUser().build();
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.APPROVED, ApplicationState.WITHDRAWN,
                                               application -> applicationWorkflowHandler.withdraw(application, internalUser));
    }

    private void assertStateChangeOnWorkflowHandlerCall(ApplicationState initialApplicationState, ApplicationState expectedApplicationState, Function<Application, Boolean> workflowHandlerMethod) {
        assertStateChangeOnWorkflowHandlerCall(initialApplicationState, expectedApplicationState, workflowHandlerMethod, null);
    }

    private void assertStateChangeOnWorkflowHandlerCall(ApplicationState initialApplicationState, ApplicationState expectedApplicationState, Function<Application, Boolean> workflowHandlerMethod, Consumer<ApplicationProcess> additionalVerifications) {
        Application application = newApplication().withApplicationState(initialApplicationState).build();
        ApplicationProcess applicationProcess = application.getApplicationProcess();
        when(applicationProcessRepositoryMock.findOneByTargetId(application.getId())).thenReturn(applicationProcess);

        assertTrue(workflowHandlerMethod.apply(application));

        assertEquals(expectedApplicationState, applicationProcess.getProcessState());

        verify(applicationProcessRepositoryMock, times(2)).findOneByTargetId(application.getId());
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