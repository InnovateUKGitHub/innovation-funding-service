package org.innovateuk.ifs.project.financecheck.workflow.financechecks;

import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.ViabilityEvent;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.domain.ViabilityProcess;
import org.innovateuk.ifs.project.financechecks.repository.ViabilityProcessRepository;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityApprovedGuard;
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
import static org.mockito.Mockito.*;


public class ViabilityWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<ViabilityWorkflowHandler, ViabilityProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;
    @Autowired
    private ViabilityApprovedGuard guard;

    private ViabilityProcessRepository viabilityProcessRepositoryMock;
    private ProjectFinanceService projectFinanceService;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        viabilityProcessRepositoryMock = (ViabilityProcessRepository) mockSupplier.apply(ViabilityProcessRepository.class);
        //BaseWorkflowHandlerIntegrationTest only supports repository mocks.
        projectFinanceService = mock(ProjectFinanceService.class);
        ReflectionTestUtils.setField(guard, "projectFinanceService", projectFinanceService);
    }

    @Test
    public void projectCreated() {
        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectUser projectUser = newProjectUser().build();

        // Call the workflow here
        boolean result = viabilityWorkflowHandler.projectCreated(partnerOrganisation, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected ViabilityProcess object (say X) and verifying that X was the object that was saved.
        ViabilityProcess expectedViabilityProcess = new ViabilityProcess(projectUser, partnerOrganisation, ViabilityState.REVIEW);

        // Ensure the correct event was fired by the workflow
        expectedViabilityProcess.setProcessEvent(ViabilityEvent.PROJECT_CREATED.getType());

        verify(viabilityProcessRepositoryMock).save(expectedViabilityProcess);
    }

    @Test
    public void viabilityApproved() {
        when(projectFinanceService.financeChecksTotals(anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource()
                .withGrantClaimPercentage(BigDecimal.valueOf(30))
                .withMaximumFundingLevel(50)
                .build(1)));

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> viabilityWorkflowHandler.viabilityApproved(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                ViabilityState.REVIEW, ViabilityState.APPROVED, ViabilityEvent.VIABILITY_APPROVED, true);
    }

    @Test
    public void viabilityReset() {
        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> viabilityWorkflowHandler.viabilityReset(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                ViabilityState.APPROVED, ViabilityState.REVIEW, ViabilityEvent.VIABILITY_RESET, true);
    }

    @Test
    public void ViabilityApproved_guard() {
        when(projectFinanceService.financeChecksTotals(anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource()
                .withGrantClaimPercentage(BigDecimal.valueOf(50))
                .withMaximumFundingLevel(30)
                .build(1)));

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> viabilityWorkflowHandler.viabilityApproved(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                ViabilityState.REVIEW, ViabilityState.APPROVED, ViabilityEvent.VIABILITY_APPROVED, false);
    }
    @Test
    public void viabilityNotApplicable() {

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> viabilityWorkflowHandler.viabilityNotApplicable(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                ViabilityState.REVIEW, ViabilityState.NOT_APPLICABLE, ViabilityEvent.VIABILITY_NOT_APPLICABLE, true);
    }

    private void callWorkflowAndCheckTransitionAndEventFired(BiFunction<PartnerOrganisation, User, Boolean> workflowMethodToCall,
                                                             ViabilityState currentViabilityState,
                                                             ViabilityState destinationViabilityState,
                                                             ViabilityEvent expectedEventToBeFired,
                                                             boolean fired) {

        Project project = newProject().build();
        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().withProject(project).build();
        User internalUser = newUser().build();

        // Set the current state in the Viability Process
        ViabilityProcess currentViabilityProcess = new ViabilityProcess((User) null, partnerOrganisation, currentViabilityState);
        when(viabilityProcessRepositoryMock.findOneByTargetId(partnerOrganisation.getId())).thenReturn(currentViabilityProcess);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(partnerOrganisation, internalUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected ViabilityProcess object (say X) and verifying that X was the object that was saved.
        ViabilityProcess expectedViabilityProcess = new ViabilityProcess(internalUser, partnerOrganisation, destinationViabilityState);

        // Ensure the correct event was fired by the workflow
        expectedViabilityProcess.setProcessEvent(expectedEventToBeFired.getType());

        if (fired) {
            verify(viabilityProcessRepositoryMock).save(expectedViabilityProcess);
        } else {
            verify(viabilityProcessRepositoryMock, never()).save(expectedViabilityProcess);
        }
    }

    @Override
    protected Class getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<ViabilityWorkflowHandler> getWorkflowHandlerType() {
        return ViabilityWorkflowHandler.class;
    }

    @Override
    protected Class<ViabilityProcessRepository> getProcessRepositoryType() {
        return ViabilityProcessRepository.class;
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(ViabilityProcessRepository.class);
        return repositories;
    }
}