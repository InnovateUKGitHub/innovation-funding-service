package org.innovateuk.ifs.project.financecheck.workflow.financechecks;

import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.domain.FundingRulesProcess;
import org.innovateuk.ifs.project.financechecks.repository.FundingRulesProcessRepository;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.*;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class FundingRulesWorkflowHandlerIntegrationTest  extends
        BaseWorkflowHandlerIntegrationTest<FundingRulesWorkflowHandler, FundingRulesProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private FundingRulesWorkflowHandler fundingRulesWorkflowHandler;

    private FundingRulesProcessRepository repository;
    private ProjectFinanceService projectFinanceService;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        repository = (FundingRulesProcessRepository) mockSupplier.apply(FundingRulesProcessRepository.class);
        //BaseWorkflowHandlerIntegrationTest only supports repository mocks.
        projectFinanceService = mock(ProjectFinanceService.class);
    }

    @Test
    public void testProjectCreated() {
        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectUser projectUser = newProjectUser().build();

        // Call the workflow here
        boolean result = fundingRulesWorkflowHandler.projectCreated(partnerOrganisation, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected FundingRulesProcess object (say X) and verifying that X was the object that was saved.
        FundingRulesProcess fundingRulesProcess = new FundingRulesProcess(projectUser, partnerOrganisation, FundingRulesState.REVIEW);

        // Ensure the correct event was fired by the workflow
        fundingRulesProcess.setProcessEvent(FundingRulesEvent.PROJECT_CREATED.getType());

        verify(repository).save(fundingRulesProcess);
    }

    @Test
    public void testFundingRulesApproved() {
        when(projectFinanceService.financeChecksTotals(anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource()
                .withGrantClaimPercentage(BigDecimal.valueOf(30))
                .withMaximumFundingLevel(50)
                .build(1)));

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> fundingRulesWorkflowHandler.fundingRulesApproved(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                FundingRulesState.REVIEW, FundingRulesState.APPROVED, FundingRulesEvent.FUNDING_RULES_APPROVED, true);
    }

    @Test
    public void testFundingRulesUpdated() {

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> fundingRulesWorkflowHandler.fundingRulesUpdated(partnerOrganisation,
                internalUser)),

                // current State, destination State and expected Event to be fired
                FundingRulesState.REVIEW, FundingRulesState.REVIEW, FundingRulesEvent.FUNDING_RULES_UPDATED, true);
    }

    private void callWorkflowAndCheckTransitionAndEventFired(BiFunction<PartnerOrganisation, User, Boolean> workflowMethodToCall,
                                                             FundingRulesState currentState,
                                                             FundingRulesState destinationState,
                                                             FundingRulesEvent expectedEventToBeFired,
                                                             boolean fired) {
        Project project = newProject().build();
        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().withProject(project).build();
        User internalUser = newUser().build();

        // Set the current state in the Funding Rules Process
        FundingRulesProcess currentProcess = new FundingRulesProcess((User) null, partnerOrganisation, currentState);
        when(repository.findOneByTargetId(partnerOrganisation.getId())).thenReturn(currentProcess);

        // Set the destination state which we expect when the event is fired

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(partnerOrganisation, internalUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected FundingRulesProcess object (say X) and verifying that X was the object that was saved.
        FundingRulesProcess expectedFundingRulesProcess = new FundingRulesProcess(internalUser, partnerOrganisation, destinationState);

        // Ensure the correct event was fired by the workflow
        expectedFundingRulesProcess.setProcessEvent(expectedEventToBeFired.getType());

        if (fired) {
            verify(repository).save(expectedFundingRulesProcess);
        } else {
            verify(repository, never()).save(expectedFundingRulesProcess);
        }
    }

    @Override
    protected Class getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<FundingRulesWorkflowHandler> getWorkflowHandlerType() {
        return FundingRulesWorkflowHandler.class;
    }

    @Override
    protected Class<FundingRulesProcessRepository> getProcessRepositoryType() {
        return FundingRulesProcessRepository.class;
    }

}
