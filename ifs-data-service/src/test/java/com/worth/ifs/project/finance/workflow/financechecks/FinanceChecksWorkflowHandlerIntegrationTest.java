package com.worth.ifs.project.finance.workflow.financechecks;

import com.worth.ifs.project.domain.PartnerOrganisation;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.finance.domain.Cost;
import com.worth.ifs.project.finance.domain.CostGroup;
import com.worth.ifs.project.finance.domain.FinanceCheck;
import com.worth.ifs.project.finance.domain.FinanceCheckProcess;
import com.worth.ifs.project.finance.repository.FinanceCheckProcessRepository;
import com.worth.ifs.project.finance.repository.FinanceCheckRepository;
import com.worth.ifs.project.finance.resource.FinanceCheckOutcomes;
import com.worth.ifs.project.finance.resource.FinanceCheckState;
import com.worth.ifs.project.finance.workflow.financechecks.configuration.FinanceCheckWorkflowHandler;
import com.worth.ifs.project.workflow.projectdetails.actions.BaseProjectDetailsAction;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import com.worth.ifs.workflow.TestableTransitionWorkflowAction;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import com.worth.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.project.finance.resource.FinanceCheckOutcomes.*;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.workflow.domain.ActivityType.PROJECT_SETUP_FINANCE_CHECKS;
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
    public void testEditFinanceCheckFigures() throws Exception {

        assertEditFinanceCheckFigures((partnerOrganisation, financeTeamUser) -> financeCheckWorkflowHandler.financeCheckFiguresEdited(partnerOrganisation, financeTeamUser),
                FINANCE_CHECK_FIGURES_EDITED);
    }

    @Test
    public void testEditFinanceCheckFiguresAndReadyForApproval() throws Exception {

        assertEditFinanceCheckFiguresAndNowReadyForApproval(
                (partnerOrganisation, financeTeamUser) -> financeCheckWorkflowHandler.financeCheckFiguresEdited(partnerOrganisation, financeTeamUser), FINANCE_CHECK_FIGURES_EDITED);
    }

    @Test
    public void testEditFinanceCheckFiguresAndReadyForApprovalWhenAlreadyInReadyForApproval() throws Exception {

        assertEditFinanceCheckFiguresAndNowReadyForApprovalFromReadyToApprove(
                (partnerOrganisation, financeTeamUser) -> financeCheckWorkflowHandler.financeCheckFiguresEdited(partnerOrganisation, financeTeamUser), FINANCE_CHECK_FIGURES_EDITED);
    }

    @Test
    public void testApprovalNotAllowedUntilAllFiguresHaveValues() {

        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().build();
        User financeTeamMember = newUser().build();

        ActivityState pendingState = new ActivityState(PROJECT_SETUP_FINANCE_CHECKS, State.PENDING);
        FinanceCheckProcess pendingProcess = new FinanceCheckProcess(financeTeamMember, partnerOrganisation, pendingState);

        when(financeCheckProcessRepositoryMock.findOneByTargetId(partnerOrganisation.getId())).thenReturn(pendingProcess);

        // now call the method under test
        assertFalse(financeCheckWorkflowHandler.approveFinanceCheckFigures(partnerOrganisation, financeTeamMember));

        verify(financeCheckProcessRepositoryMock, atLeastOnce()).findOneByTargetId(partnerOrganisation.getId());

        verify(financeCheckProcessRepositoryMock, never()).save(any(FinanceCheckProcess.class));

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void testSubmissionNotAllowedUntilMandatoryValuesAvailableCheckMethod() {

        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().build();
        User financeTeamUser = newUser().build();

        ActivityState pendingState = new ActivityState(PROJECT_SETUP_FINANCE_CHECKS, State.PENDING);
        FinanceCheckProcess pendingProcess = new FinanceCheckProcess(financeTeamUser, partnerOrganisation, pendingState);

        when(financeCheckProcessRepositoryMock.findOneByTargetId(partnerOrganisation.getId())).thenReturn(pendingProcess);

        // now call the method under test
        assertFalse(financeCheckWorkflowHandler.isApprovalAllowed(partnerOrganisation));

        verify(financeCheckProcessRepositoryMock, atLeastOnce()).findOneByTargetId(partnerOrganisation.getId());

        verify(financeCheckProcessRepositoryMock, never()).save(any(FinanceCheckProcess.class));

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void testApprovalNotAllowedUntilAllFinanceFiguresEnteredButInCorrectState() {

        Project project = newProject().build();
        Organisation organisation = newOrganisation().build();

        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().
                withProject(project).
                withOrganisation(organisation).
                build();

        User financeTeamUser = newUser().build();

        ActivityState readyToSubmitState = new ActivityState(PROJECT_SETUP_FINANCE_CHECKS, State.READY_TO_SUBMIT);
        FinanceCheckProcess readyToSubmitProcess = new FinanceCheckProcess(financeTeamUser, partnerOrganisation, readyToSubmitState);

        when(financeCheckProcessRepositoryMock.findOneByTargetId(partnerOrganisation.getId())).thenReturn(readyToSubmitProcess);

        CostGroup costGroup = new CostGroup("Finance Check figures", asList(new Cost(), new Cost("2")));
        FinanceCheck financeCheck = new FinanceCheck(project, costGroup);
        when(financeCheckRepositoryMock.findByProjectIdAndOrganisationId(
                partnerOrganisation.getProject().getId(), partnerOrganisation.getOrganisation().getId())).thenReturn(financeCheck);

        // now call the method under test
        assertTrue(financeCheckWorkflowHandler.approveFinanceCheckFigures(partnerOrganisation, financeTeamUser));

        verify(financeCheckProcessRepositoryMock, atLeastOnce()).findOneByTargetId(partnerOrganisation.getId());

        verify(financeCheckProcessRepositoryMock, never()).save(any(FinanceCheckProcess.class));

        verify(financeCheckRepositoryMock).findByProjectIdAndOrganisationId(
                partnerOrganisation.getProject().getId(),
                partnerOrganisation.getOrganisation().getId());

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void testApprovalAllowedWhenAllFinanceFiguresEntered() {

        Project project = newProject().build();
        Organisation organisation = newOrganisation().build();

        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().
                withProject(project).
                withOrganisation(organisation).
                build();

        User financeTeamUser = newUser().build();

        ActivityState readyToSubmitState = new ActivityState(PROJECT_SETUP_FINANCE_CHECKS, State.READY_TO_SUBMIT);
        FinanceCheckProcess readyToSubmitProcess = new FinanceCheckProcess(financeTeamUser, partnerOrganisation, readyToSubmitState);

        ActivityState approvedState = new ActivityState(PROJECT_SETUP_FINANCE_CHECKS, State.ACCEPTED);

        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_FINANCE_CHECKS, State.ACCEPTED)).thenReturn(approvedState);

        when(financeCheckProcessRepositoryMock.findOneByTargetId(partnerOrganisation.getId())).thenReturn(readyToSubmitProcess);

        CostGroup costGroup = new CostGroup("Finance Check figures", asList(new Cost("1"), new Cost("2")));
        FinanceCheck financeCheck = new FinanceCheck(project, costGroup);
        when(financeCheckRepositoryMock.findByProjectIdAndOrganisationId(
                partnerOrganisation.getProject().getId(), partnerOrganisation.getOrganisation().getId())).thenReturn(financeCheck);

        // now call the method under test
        assertTrue(financeCheckWorkflowHandler.approveFinanceCheckFigures(partnerOrganisation, financeTeamUser));

        verify(activityStateRepositoryMock, atLeastOnce()).findOneByActivityTypeAndState(PROJECT_SETUP_FINANCE_CHECKS, State.ACCEPTED);

        verify(financeCheckProcessRepositoryMock, atLeastOnce()).findOneByTargetId(partnerOrganisation.getId());

        verify(financeCheckProcessRepositoryMock).save(processExpectations(partnerOrganisation.getId(), null, financeTeamUser.getId(), FinanceCheckState.APPROVED, APPROVE));

        verify(financeCheckRepositoryMock).findByProjectIdAndOrganisationId(
                partnerOrganisation.getProject().getId(),
                partnerOrganisation.getOrganisation().getId());

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void testApprovalAllowedWhenAllFinanceCheckFiguresEnteredCheckDoesNotUpdateAnything() {

        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().build();
        User financeTeamUser = newUser().build();

        ActivityState readyToSubmitState = new ActivityState(PROJECT_SETUP_FINANCE_CHECKS, State.READY_TO_SUBMIT);
        FinanceCheckProcess readyToSubmitProcess = new FinanceCheckProcess(financeTeamUser, partnerOrganisation, readyToSubmitState);

        when(financeCheckProcessRepositoryMock.findOneByTargetId(partnerOrganisation.getId())).thenReturn(readyToSubmitProcess);

        // now call the method under test
        assertTrue(financeCheckWorkflowHandler.isApprovalAllowed(partnerOrganisation));

        verify(financeCheckProcessRepositoryMock, atLeastOnce()).findOneByTargetId(partnerOrganisation.getId());

        verifyNoMoreInteractionsWithMocks();
    }

    /**
     * Test that adding one of the mandatory values prior to being able to submit the project details works and keeps the
     * state in pending until ready to submit
     */
    private void assertEditFinanceCheckFigures(BiFunction<PartnerOrganisation, User, Boolean> handlerMethod, FinanceCheckOutcomes expectedEvent) {

        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().build();
        User financeTeamUser = newUser().build();

        ActivityState pendingState = new ActivityState(PROJECT_SETUP_FINANCE_CHECKS, State.PENDING);
        FinanceCheckProcess pendingProcess = new FinanceCheckProcess((User) null, partnerOrganisation, pendingState);

        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_FINANCE_CHECKS, State.PENDING)).thenReturn(pendingState);

        when(financeCheckProcessRepositoryMock.findOneByTargetId(partnerOrganisation.getId())).thenReturn(pendingProcess);

        // now call the method under test
        assertTrue(handlerMethod.apply(partnerOrganisation, financeTeamUser));

        verify(activityStateRepositoryMock, atLeastOnce()).findOneByActivityTypeAndState(PROJECT_SETUP_FINANCE_CHECKS, State.PENDING);

        verify(financeCheckProcessRepositoryMock, atLeastOnce()).findOneByTargetId(partnerOrganisation.getId());

        verify(financeCheckProcessRepositoryMock).save(
                processExpectations(partnerOrganisation.getId(), null, financeTeamUser.getId(), FinanceCheckState.PENDING, expectedEvent));

        verifyNoMoreInteractionsWithMocks();
    }

    private void assertEditFinanceCheckFiguresAndNowReadyForApproval(BiFunction<PartnerOrganisation, User, Boolean> handlerFn, FinanceCheckOutcomes expectedEvent) {
        assertEditFinanceCheckFiguresAndNowReadyForApproval(FinanceCheckState.READY_TO_APPROVE, handlerFn, expectedEvent);
    }

    private void assertEditFinanceCheckFiguresAndNowReadyForApprovalFromReadyToApprove(BiFunction<PartnerOrganisation, User, Boolean> handlerFn, FinanceCheckOutcomes expectedEvent) {
        assertEditFinanceCheckFiguresAndNowReadyForApproval(FinanceCheckState.PENDING, handlerFn, expectedEvent);
    }

        /**
         * This asserts that triggering the given handler method with a fully filled in Project will move the process into
         * Ready to Submit because all the mandatory values are now provided
         */
    private void assertEditFinanceCheckFiguresAndNowReadyForApproval(FinanceCheckState originalState, BiFunction<PartnerOrganisation, User, Boolean> handlerFn, FinanceCheckOutcomes expectedEvent) {

        Project project = newProject().build();
        Organisation organisation = newOrganisation().build();

        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().
                withProject(project).
                withOrganisation(organisation).
                build();

        User financeTeamMember = newUser().build();

        ActivityState originalActivityState = new ActivityState(PROJECT_SETUP_FINANCE_CHECKS, originalState.getBackingState());
        FinanceCheckProcess originalProcess = new FinanceCheckProcess((User) null, partnerOrganisation, originalActivityState);
        FinanceCheckProcess updatedProcess = new FinanceCheckProcess((User) null, partnerOrganisation, originalActivityState);

        ActivityState readyToSubmitState = new ActivityState(PROJECT_SETUP_FINANCE_CHECKS, State.READY_TO_SUBMIT);

        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_FINANCE_CHECKS, State.READY_TO_SUBMIT)).thenReturn(readyToSubmitState);

        when(financeCheckProcessRepositoryMock.findOneByTargetId(partnerOrganisation.getId())).thenReturn(originalProcess);
        when(financeCheckProcessRepositoryMock.findOneByTargetId(partnerOrganisation.getId())).thenReturn(updatedProcess);

        CostGroup costGroup = new CostGroup("Finance Check figures", asList(new Cost("1"), new Cost("2")));
        FinanceCheck financeCheck = new FinanceCheck(project, costGroup);
        when(financeCheckRepositoryMock.findByProjectIdAndOrganisationId(
                partnerOrganisation.getProject().getId(), partnerOrganisation.getOrganisation().getId())).thenReturn(financeCheck);

        // now call the method under test
        assertTrue(handlerFn.apply(partnerOrganisation, financeTeamMember));

        verify(activityStateRepositoryMock, atLeastOnce()).findOneByActivityTypeAndState(PROJECT_SETUP_FINANCE_CHECKS, State.READY_TO_SUBMIT);

        verify(financeCheckProcessRepositoryMock, atLeastOnce()).findOneByTargetId(partnerOrganisation.getId());

        verify(financeCheckProcessRepositoryMock).save(processExpectations(partnerOrganisation.getId(), null, financeTeamMember.getId(), FinanceCheckState.READY_TO_APPROVE, expectedEvent));

        verify(financeCheckRepositoryMock).findByProjectIdAndOrganisationId(
                partnerOrganisation.getProject().getId(),
                partnerOrganisation.getOrganisation().getId());

        verifyNoMoreInteractionsWithMocks();
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