package org.innovateuk.ifs.project.finance.workflow.financechecks;

import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.domain.Cost;
import org.innovateuk.ifs.project.finance.domain.CostGroup;
import org.innovateuk.ifs.project.finance.domain.FinanceCheck;
import org.innovateuk.ifs.project.finance.domain.FinanceCheckProcess;
import org.innovateuk.ifs.project.finance.repository.FinanceCheckProcessRepository;
import org.innovateuk.ifs.project.finance.repository.FinanceCheckRepository;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOutcomes;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckState;
import org.innovateuk.ifs.project.finance.workflow.financechecks.configuration.FinanceCheckWorkflowHandler;
import org.innovateuk.ifs.project.workflow.projectdetails.actions.BaseProjectDetailsAction;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.finance.resource.FinanceCheckOutcomes.*;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP_FINANCE_CHECKS;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FinanceChecksWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<FinanceCheckWorkflowHandler, FinanceCheckProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private FinanceCheckWorkflowHandler financeCheckWorkflowHandler;

    private ActivityStateRepository activityStateRepositoryMock;
    private FinanceCheckProcessRepository financeCheckProcessRepositoryMock;
    private FinanceCheckRepository financeCheckRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        financeCheckProcessRepositoryMock = (FinanceCheckProcessRepository) mockSupplier.apply(getProcessRepositoryType());
        financeCheckRepositoryMock = (FinanceCheckRepository) mockSupplier.apply(FinanceCheckRepository.class);
    }

    @Test
    public void testProjectCreated() throws Exception {

        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState pendingState = new ActivityState(PROJECT_SETUP_FINANCE_CHECKS, State.PENDING);
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_FINANCE_CHECKS, State.PENDING)).thenReturn(pendingState);

        // this first step will not have access to an existing Process, because it's just starting
        when(financeCheckProcessRepositoryMock.findOneByTargetId(partnerOrganisation.getId())).thenReturn(null);

        // now call the method under test
        assertTrue(financeCheckWorkflowHandler.projectCreated(partnerOrganisation, projectUser));

        verify(financeCheckProcessRepositoryMock).findOneByTargetId(partnerOrganisation.getId());

        verify(activityStateRepositoryMock).findOneByActivityTypeAndState(PROJECT_SETUP_FINANCE_CHECKS, State.PENDING);

        verify(financeCheckProcessRepositoryMock).save(
                processExpectations(partnerOrganisation.getId(), projectUser.getId(), null, FinanceCheckState.PENDING, PROJECT_CREATED));

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void testApproveFinanceCheck() throws Exception {
    //TODO 6716 need test to assert approve is correctly assigned
    }

    private FinanceCheckProcess processExpectations(Long expectedProjectId, Long expectedProjectUserId, Long expectedUserId, FinanceCheckState expectedState, FinanceCheckOutcomes expectedEvent) {
        return createLambdaMatcher(process -> {
            assertProcessState(expectedProjectId, expectedProjectUserId, expectedUserId, expectedState, expectedEvent, process);
        });
    }

    private void assertProcessState(Long expectedProjectId, Long expectedProjectUserId, Long expectedUserId, FinanceCheckState expectedState, FinanceCheckOutcomes expectedEvent, FinanceCheckProcess process) {
        assertEquals(expectedProjectId, process.getTarget().getId());
        assertEquals(expectedProjectUserId, ofNullable(process.getParticipant()).map(ProjectUser::getId).orElse(null));
        assertEquals(expectedUserId, ofNullable(process.getInternalParticipant()).map(User::getId).orElse(null));
        assertEquals(expectedState, process.getActivityState());
        assertEquals(expectedEvent.getType(), process.getProcessEvent());
    }

    @Override
    protected Class getBaseActionType() {
        return BaseProjectDetailsAction.class;
    }

    @Override
    protected Class<FinanceCheckWorkflowHandler> getWorkflowHandlerType() {
        return FinanceCheckWorkflowHandler.class;
    }

    @Override
    protected Class<FinanceCheckProcessRepository> getProcessRepositoryType() {
        return FinanceCheckProcessRepository.class;
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(FinanceCheckRepository.class);
        return repositories;
    }
}
