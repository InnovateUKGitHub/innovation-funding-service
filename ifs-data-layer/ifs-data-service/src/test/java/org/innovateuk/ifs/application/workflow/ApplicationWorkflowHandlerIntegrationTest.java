package org.innovateuk.ifs.application.workflow;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.repository.ApplicationProcessRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.AutoCompleteSectionsUtil;
import org.innovateuk.ifs.application.workflow.actions.AutoCompleteSectionsAction;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.IneligibleOutcomeBuilder.newIneligibleOutcome;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class ApplicationWorkflowHandlerIntegrationTest extends BaseWorkflowHandlerIntegrationTest<ApplicationWorkflowHandler, ApplicationProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private ApplicationWorkflowHandler applicationWorkflowHandler;

    @Autowired
    private AutoCompleteSectionsAction autoCompleteSectionsAction;

    private ApplicationProcessRepository applicationProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        applicationProcessRepositoryMock = (ApplicationProcessRepository) mockSupplier.apply(ApplicationProcessRepository.class);
        setField(autoCompleteSectionsAction, "autoCompleteSectionsUtil", mock(AutoCompleteSectionsUtil.class));
    }

    @Test
    public void open() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.CREATED, ApplicationState.OPENED, applicationWorkflowHandler::open);
    }

    @Test
    public void submit() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationState.OPENED, ApplicationState.SUBMITTED, applicationWorkflowHandler::submit);
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

    private void assertStateChangeOnWorkflowHandlerCall(ApplicationState initialApplicationState, ApplicationState expectedApplicationState, Function<Application, Boolean> workflowHandlerMethod) {
        assertStateChangeOnWorkflowHandlerCall(initialApplicationState, expectedApplicationState, workflowHandlerMethod, null);
    }

    private void assertStateChangeOnWorkflowHandlerCall(ApplicationState initialApplicationState, ApplicationState expectedApplicationState, Function<Application, Boolean> workflowHandlerMethod, Consumer<ApplicationProcess> additionalVerifications) {
        Application application = newApplication()
                .withCompetition(newCompetition().withCompetitionType(newCompetitionType().build()).build())
                .withProcessRoles(newProcessRole().withRole(ProcessRoleType.LEADAPPLICANT).withOrganisationId(100L).build())
                .withApplicationState(initialApplicationState).build();
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