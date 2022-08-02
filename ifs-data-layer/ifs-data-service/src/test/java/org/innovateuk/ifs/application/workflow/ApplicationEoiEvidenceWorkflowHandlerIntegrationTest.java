package org.innovateuk.ifs.application.workflow;

import org.innovateuk.ifs.application.domain.*;
import org.innovateuk.ifs.application.repository.ApplicationEoiEvidenceProcessRepository;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationEoiEvidenceWorkflowHandler;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.Random;
import java.util.function.Function;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class ApplicationEoiEvidenceWorkflowHandlerIntegrationTest extends BaseWorkflowHandlerIntegrationTest<ApplicationEoiEvidenceWorkflowHandler, ApplicationEoiEvidenceProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private ApplicationEoiEvidenceWorkflowHandler applicationEoiEvidenceWorkflowHandler;

    private ApplicationEoiEvidenceProcessRepository applicationEoiEvidenceProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        applicationEoiEvidenceProcessRepositoryMock = (ApplicationEoiEvidenceProcessRepository) mockSupplier.apply(ApplicationEoiEvidenceProcessRepository.class);
    }

    @Test
    public void submit() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationEoiEvidenceState.NOT_SUBMITTED, ApplicationEoiEvidenceState.SUBMITTED,
                applicationEoiEvidenceResponse -> applicationEoiEvidenceWorkflowHandler.submit(applicationEoiEvidenceResponse, createProcessRole(), createUser()));
    }

    @Test
    public void documentUploaded() {
        assertStateChangeOnWorkflowHandlerCall(ApplicationEoiEvidenceState.CREATED, ApplicationEoiEvidenceState.NOT_SUBMITTED, applicationEoiEvidenceWorkflowHandler::documentUploaded);
    }

    private ProcessRole createProcessRole() {
        return newProcessRole().build();
    }

    private User createUser() {
        return newUser().build();
    }

    @Override
    protected Class<TestableTransitionWorkflowAction> getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<ApplicationEoiEvidenceWorkflowHandler> getWorkflowHandlerType() {
        return ApplicationEoiEvidenceWorkflowHandler.class;
    }

    @Override
    protected Class<ApplicationEoiEvidenceProcessRepository> getProcessRepositoryType() {
        return ApplicationEoiEvidenceProcessRepository.class;
    }

    private void assertStateChangeOnWorkflowHandlerCall(ApplicationEoiEvidenceState initialEoiEvidenceState, ApplicationEoiEvidenceState expectedEoiEvidenceState,
                                                        Function<ApplicationEoiEvidenceResponse, Boolean> workflowHandlerMethod) {
        Application application = newApplication()
                .withCompetition(newCompetition().withCompetitionType(newCompetitionType().build()).build())
                .withProcessRoles(newProcessRole().withRole(ProcessRoleType.LEADAPPLICANT).withOrganisationId(100L).build())
                .build();
        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = ApplicationEoiEvidenceResponse.builder()
                .id(new Random().nextLong())
                .application(application)
                .build();
        setApplicationEoiEvidenceInitialState(applicationEoiEvidenceResponse, initialEoiEvidenceState);
        ApplicationEoiEvidenceProcess applicationEoiEvidenceProcess = applicationEoiEvidenceResponse.getApplicationEoiEvidenceProcess();
        when(applicationEoiEvidenceProcessRepositoryMock.findOneByTargetId(applicationEoiEvidenceResponse.getId())).thenReturn(applicationEoiEvidenceProcess);

        assertTrue(workflowHandlerMethod.apply(applicationEoiEvidenceResponse));

        assertEquals(expectedEoiEvidenceState, applicationEoiEvidenceProcess.getProcessState());

        verify(applicationEoiEvidenceProcessRepositoryMock, times(2)).findOneByTargetId(applicationEoiEvidenceResponse.getId());
        verify(applicationEoiEvidenceProcessRepositoryMock).save(applicationEoiEvidenceProcess);

        verifyNoMoreInteractionsWithMocks();
    }

    private void setApplicationEoiEvidenceInitialState(ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse, ApplicationEoiEvidenceState applicationEoiEvidenceState) {
        ApplicationEoiEvidenceProcess applicationEoiEvidenceProcess = new ApplicationEoiEvidenceProcess(null, applicationEoiEvidenceResponse, applicationEoiEvidenceState);
        setField("applicationEoiEvidenceProcess", applicationEoiEvidenceProcess, applicationEoiEvidenceResponse);
    }
}
