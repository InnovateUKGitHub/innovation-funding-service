package org.innovateuk.ifs.project.financecheck.workflow.financechecks;

import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.EligibilityEvent;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.financechecks.domain.EligibilityProcess;
import org.innovateuk.ifs.project.financechecks.repository.EligibilityProcessRepository;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityApprovedGuard;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class EligibilityWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<EligibilityWorkflowHandler, EligibilityProcessRepository, TestableTransitionWorkflowAction> {



    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;
    @Autowired
    private EligibilityApprovedGuard guard;

    private EligibilityProcessRepository eligibilityProcessRepositoryMock;
    private ProjectFinanceService projectFinanceService;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        eligibilityProcessRepositoryMock = (EligibilityProcessRepository) mockSupplier.apply(EligibilityProcessRepository.class);
        //BaseWorkflowHandlerIntegrationTest only supports repository mocks.
        projectFinanceService = mock(ProjectFinanceService.class);
        ReflectionTestUtils.setField(guard, "projectFinanceService", projectFinanceService);
    }

    @Test
    public void testProjectCreated() {
        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectUser projectUser = newProjectUser().build();

        // Call the workflow here
        boolean result = eligibilityWorkflowHandler.projectCreated(partnerOrganisation, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected EligibilityProcess object (say X) and verifying that X was the object that was saved.
        EligibilityProcess expectedEligibilityProcess = new EligibilityProcess(projectUser, partnerOrganisation, EligibilityState.REVIEW);

        // Ensure the correct event was fired by the workflow
        expectedEligibilityProcess.setProcessEvent(EligibilityEvent.PROJECT_CREATED.getType());

        verify(eligibilityProcessRepositoryMock).save(expectedEligibilityProcess);
    }

    @Test
    public void testEligibilityApproved() {
        when(projectFinanceService.financeChecksTotals(anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource()
                .withGrantClaimPercentage(BigDecimal.valueOf(30))
                .withMaximumFundingLevel(50)
                .build(1)));

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> eligibilityWorkflowHandler.eligibilityApproved(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                EligibilityState.REVIEW, EligibilityState.APPROVED, EligibilityEvent.ELIGIBILITY_APPROVED, true);
    }

    @Test
    public void testEligibilityApproved_guard() {
        when(projectFinanceService.financeChecksTotals(anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource()
                .withGrantClaimPercentage(BigDecimal.valueOf(50))
                .withMaximumFundingLevel(30)
                .build(1)));

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> eligibilityWorkflowHandler.eligibilityApproved(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                EligibilityState.REVIEW, EligibilityState.APPROVED, EligibilityEvent.ELIGIBILITY_APPROVED, false);
    }

    @Test
    public void EligibilityReset() {

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> eligibilityWorkflowHandler.eligibilityReset(partnerOrganisation,
            internalUser)),

            // current State, destination State and expected Event to be fired
            EligibilityState.APPROVED, EligibilityState.REVIEW, EligibilityEvent.ELIGIBILITY_RESET, true);
    }

    @Test
    public void testNotRequestingFunding() {

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> eligibilityWorkflowHandler.notRequestingFunding(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                EligibilityState.REVIEW, EligibilityState.NOT_APPLICABLE, EligibilityEvent.NOT_REQUESTING_FUNDING, true);
    }

    private void callWorkflowAndCheckTransitionAndEventFired(BiFunction<PartnerOrganisation, User, Boolean> workflowMethodToCall,
                                                             EligibilityState currentEligibilityState,
                                                             EligibilityState destinationEligibilityState,
                                                             EligibilityEvent expectedEventToBeFired,
                                                             boolean fired) {
        Project project = newProject().build();
        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().withProject(project).build();
        User internalUser = newUser().build();

        // Set the current state in the Eligibility Process
        EligibilityProcess currentEligibilityProcess = new EligibilityProcess((User) null, partnerOrganisation, currentEligibilityState);
        when(eligibilityProcessRepositoryMock.findOneByTargetId(partnerOrganisation.getId())).thenReturn(currentEligibilityProcess);

        // Set the destination state which we expect when the event is fired

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(partnerOrganisation, internalUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected EligibilityProcess object (say X) and verifying that X was the object that was saved.
        EligibilityProcess expectedEligibilityProcess = new EligibilityProcess(internalUser, partnerOrganisation, destinationEligibilityState);

        // Ensure the correct event was fired by the workflow
        expectedEligibilityProcess.setProcessEvent(expectedEventToBeFired.getType());

        if (fired) {
            verify(eligibilityProcessRepositoryMock).save(expectedEligibilityProcess);
        } else {
            verify(eligibilityProcessRepositoryMock, never()).save(expectedEligibilityProcess);
        }
    }

    @Override
    protected Class getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<EligibilityWorkflowHandler> getWorkflowHandlerType() {
        return EligibilityWorkflowHandler.class;
    }

    @Override
    protected Class<EligibilityProcessRepository> getProcessRepositoryType() {
        return EligibilityProcessRepository.class;
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(EligibilityProcessRepository.class);
        return repositories;
    }
}