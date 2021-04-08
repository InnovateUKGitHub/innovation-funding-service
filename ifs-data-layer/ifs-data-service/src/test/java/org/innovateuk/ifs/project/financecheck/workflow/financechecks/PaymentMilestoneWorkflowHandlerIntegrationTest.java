package org.innovateuk.ifs.project.financecheck.workflow.financechecks;

import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneEvent;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneState;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.domain.PaymentMilestoneProcess;
import org.innovateuk.ifs.project.financechecks.repository.EligibilityProcessRepository;
import org.innovateuk.ifs.project.financechecks.repository.PaymentMilestoneProcessRepository;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.PaymentMilestoneApprovedGuard;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.PaymentMilestoneWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class PaymentMilestoneWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<PaymentMilestoneWorkflowHandler, PaymentMilestoneProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private PaymentMilestoneWorkflowHandler paymentMilestoneWorkflowHandler;

    @Autowired
    private PaymentMilestoneApprovedGuard guard;

    private PaymentMilestoneProcessRepository repository;
    private ProjectFinanceService projectFinanceService;
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        repository = (PaymentMilestoneProcessRepository) mockSupplier.apply(PaymentMilestoneProcessRepository.class);
        //BaseWorkflowHandlerIntegrationTest only supports repository mocks.
        projectFinanceService = mock(ProjectFinanceService.class);
        eligibilityWorkflowHandler = mock(EligibilityWorkflowHandler.class);
        viabilityWorkflowHandler = mock(ViabilityWorkflowHandler.class);
        ReflectionTestUtils.setField(guard, "projectFinanceService", projectFinanceService);
        ReflectionTestUtils.setField(guard, "eligibilityWorkflowHandler", eligibilityWorkflowHandler);
        ReflectionTestUtils.setField(guard, "viabilityWorkflowHandler", viabilityWorkflowHandler);
    }

    @Test
    public void testProjectCreated() {
        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectUser projectUser = newProjectUser().build();

        // Call the workflow here
        boolean result = paymentMilestoneWorkflowHandler.projectCreated(partnerOrganisation, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected EligibilityProcess object (say X) and verifying that X was the object that was saved.
        PaymentMilestoneProcess paymentMilestoneProcess = new PaymentMilestoneProcess(projectUser, partnerOrganisation, PaymentMilestoneState.REVIEW);

        // Ensure the correct event was fired by the workflow
        paymentMilestoneProcess.setProcessEvent(PaymentMilestoneEvent.PROJECT_CREATED.getType());

        verify(repository).save(paymentMilestoneProcess);
    }

    @Test
    public void testPaymentMilestoneApproved() {
        when(projectFinanceService.financeChecksTotals(anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource()
                .withGrantClaimPercentage(BigDecimal.valueOf(30))
                .withMaximumFundingLevel(50)
                .build(1)));

        when(eligibilityWorkflowHandler.getState(any())).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandler.getState(any())).thenReturn(ViabilityState.APPROVED);

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> paymentMilestoneWorkflowHandler.paymentMilestoneApproved(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                PaymentMilestoneState.REVIEW, PaymentMilestoneState.APPROVED, PaymentMilestoneEvent.PAYMENT_MILESTONE_APPROVED, true);
    }

    @Test
    public void testPaymentMilestoneApproved_guard() {
        when(projectFinanceService.financeChecksTotals(anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource()
                .withGrantClaimPercentage(BigDecimal.valueOf(50))
                .withMaximumFundingLevel(30)
                .build(1)));

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> paymentMilestoneWorkflowHandler.paymentMilestoneApproved(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                PaymentMilestoneState.REVIEW, PaymentMilestoneState.APPROVED, PaymentMilestoneEvent.PAYMENT_MILESTONE_APPROVED, false);
    }

    @Test
    public void paymentMilestoneReset() {

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> paymentMilestoneWorkflowHandler.paymentMilestoneReset(partnerOrganisation,
                internalUser, null)),

                // current State, destination State and expected Event to be fired
                PaymentMilestoneState.APPROVED, PaymentMilestoneState.REVIEW, PaymentMilestoneEvent.PAYMENT_MILESTONE_RESET, true);
    }

    private void callWorkflowAndCheckTransitionAndEventFired(BiFunction<PartnerOrganisation, User, Boolean> workflowMethodToCall,
                                                             PaymentMilestoneState currentState,
                                                             PaymentMilestoneState destinationState,
                                                             PaymentMilestoneEvent expectedEventToBeFired,
                                                             boolean fired) {
        Project project = newProject().build();
        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().withProject(project).build();
        User internalUser = newUser().build();

        // Set the current state in the Eligibility Process
        PaymentMilestoneProcess currentProcess = new PaymentMilestoneProcess((User) null, partnerOrganisation, currentState);
        when(repository.findOneByTargetId(partnerOrganisation.getId())).thenReturn(currentProcess);

        // Set the destination state which we expect when the event is fired

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(partnerOrganisation, internalUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected EligibilityProcess object (say X) and verifying that X was the object that was saved.
        PaymentMilestoneProcess expectedEligibilityProcess = new PaymentMilestoneProcess(internalUser, partnerOrganisation, destinationState);

        // Ensure the correct event was fired by the workflow
        expectedEligibilityProcess.setProcessEvent(expectedEventToBeFired.getType());

        if (fired) {
            verify(repository).save(expectedEligibilityProcess);
        } else {
            verify(repository, never()).save(expectedEligibilityProcess);
        }
    }

    @Override
    protected Class getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<PaymentMilestoneWorkflowHandler> getWorkflowHandlerType() {
        return PaymentMilestoneWorkflowHandler.class;
    }

    @Override
    protected Class<PaymentMilestoneProcessRepository> getProcessRepositoryType() {
        return PaymentMilestoneProcessRepository.class;
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(EligibilityProcessRepository.class);
        return repositories;
    }
}
