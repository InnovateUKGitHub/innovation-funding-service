package org.innovateuk.ifs.project.financecheck.workflow.financechecks;

import org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.financechecks.domain.ViabilityProcess;
import org.innovateuk.ifs.project.financechecks.repository.ViabilityProcessRepository;
import org.innovateuk.ifs.project.finance.resource.ViabilityOutcomes;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP_VIABILITY;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ViabilityWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<ViabilityWorkflowHandler, ViabilityProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;
    private ActivityStateRepository activityStateRepositoryMock;
    private ViabilityProcessRepository viabilityProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        viabilityProcessRepositoryMock = (ViabilityProcessRepository) mockSupplier.apply(ViabilityProcessRepository.class);
    }

    @Test
    public void testProjectCreated() throws Exception {

        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_VIABILITY, ViabilityState.REVIEW.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_VIABILITY, ViabilityState.REVIEW.getBackingState())).thenReturn(expectedActivityState);


        // Call the workflow here
        boolean result = viabilityWorkflowHandler.projectCreated(partnerOrganisation, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected ViabilityProcess object (say X) and verifying that X was the object that was saved.
        ViabilityProcess expectedViabilityProcess = new ViabilityProcess(projectUser, partnerOrganisation, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedViabilityProcess.setProcessEvent(ViabilityOutcomes.PROJECT_CREATED.getType());

        verify(viabilityProcessRepositoryMock).save(expectedViabilityProcess);

    }

    @Test
    public void testViabilityApproved() throws Exception {

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> viabilityWorkflowHandler.viabilityApproved(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                ViabilityState.REVIEW, ViabilityState.APPROVED, ViabilityOutcomes.VIABILITY_APPROVED);
    }

    @Test
    public void testOrganisationIsAcademic() throws Exception {

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> viabilityWorkflowHandler.organisationIsAcademic(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                ViabilityState.REVIEW, ViabilityState.NOT_APPLICABLE, ViabilityOutcomes.ORGANISATION_IS_ACADEMIC);
    }

    private void callWorkflowAndCheckTransitionAndEventFired(BiFunction<PartnerOrganisation, User, Boolean> workflowMethodToCall,
                                                             ViabilityState currentViabilityState,
                                                             ViabilityState destinationViabilityState,
                                                             ViabilityOutcomes expectedEventToBeFired) {

        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        User internalUser = newUser().build();

        // Set the current state in the Viability Process
        ActivityState currentActivityState = new ActivityState(PROJECT_SETUP_VIABILITY, currentViabilityState.getBackingState());
        ViabilityProcess currentViabilityProcess = new ViabilityProcess((User) null, partnerOrganisation, currentActivityState);
        when(viabilityProcessRepositoryMock.findOneByTargetId(partnerOrganisation.getId())).thenReturn(currentViabilityProcess);

        // Set the destination state which we expect when the event is fired
        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_VIABILITY, destinationViabilityState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_VIABILITY, destinationViabilityState.getBackingState())).thenReturn(expectedActivityState);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(partnerOrganisation, internalUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected ViabilityProcess object (say X) and verifying that X was the object that was saved.
        ViabilityProcess expectedViabilityProcess = new ViabilityProcess(internalUser, partnerOrganisation, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedViabilityProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(viabilityProcessRepositoryMock).save(expectedViabilityProcess);
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
        repositories.add(ActivityStateRepository.class);
        return repositories;
    }
}
