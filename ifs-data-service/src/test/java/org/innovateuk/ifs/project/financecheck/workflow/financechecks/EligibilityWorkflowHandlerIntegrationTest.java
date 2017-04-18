package org.innovateuk.ifs.project.financecheck.workflow.financechecks;

import org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.financecheck.domain.EligibilityProcess;
import org.innovateuk.ifs.project.financecheck.repository.EligibilityProcessRepository;
import org.innovateuk.ifs.project.finance.resource.EligibilityOutcomes;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.financecheck.workflow.financechecks.configuration.EligibilityWorkflowHandler;
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
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP_ELIGIBILITY;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class
EligibilityWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<EligibilityWorkflowHandler, EligibilityProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;
    private ActivityStateRepository activityStateRepositoryMock;
    private EligibilityProcessRepository eligibilityProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        eligibilityProcessRepositoryMock = (EligibilityProcessRepository) mockSupplier.apply(EligibilityProcessRepository.class);
    }

    @Test
    public void testProjectCreated() throws Exception {

        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_ELIGIBILITY, EligibilityState.REVIEW.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_ELIGIBILITY, EligibilityState.REVIEW.getBackingState())).thenReturn(expectedActivityState);


        // Call the workflow here
        boolean result = eligibilityWorkflowHandler.projectCreated(partnerOrganisation, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected EligibilityProcess object (say X) and verifying that X was the object that was saved.
        EligibilityProcess expectedEligibilityProcess = new EligibilityProcess(projectUser, partnerOrganisation, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedEligibilityProcess.setProcessEvent(EligibilityOutcomes.PROJECT_CREATED.getType());

        verify(eligibilityProcessRepositoryMock).save(expectedEligibilityProcess);

    }

    @Test
    public void testEligibilityApproved() throws Exception {

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> eligibilityWorkflowHandler.eligibilityApproved(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                EligibilityState.REVIEW, EligibilityState.APPROVED, EligibilityOutcomes.ELIGIBILITY_APPROVED);
    }

    @Test
    public void testNotRequestingFunding() throws Exception {

        callWorkflowAndCheckTransitionAndEventFired(((partnerOrganisation, internalUser) -> eligibilityWorkflowHandler.notRequestingFunding(partnerOrganisation, internalUser)),

                // current State, destination State and expected Event to be fired
                EligibilityState.REVIEW, EligibilityState.NOT_APPLICABLE, EligibilityOutcomes.NOT_REQUESTING_FUNDING);
    }

    private void callWorkflowAndCheckTransitionAndEventFired(BiFunction<PartnerOrganisation, User, Boolean> workflowMethodToCall,
                                                             EligibilityState currentEligibilityState,
                                                             EligibilityState destinationEligibilityState,
                                                             EligibilityOutcomes expectedEventToBeFired) {

        PartnerOrganisation partnerOrganisation = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        User internalUser = newUser().build();

        // Set the current state in the Eligibility Process
        ActivityState currentActivityState = new ActivityState(PROJECT_SETUP_ELIGIBILITY, currentEligibilityState.getBackingState());
        EligibilityProcess currentEligibilityProcess = new EligibilityProcess((User) null, partnerOrganisation, currentActivityState);
        when(eligibilityProcessRepositoryMock.findOneByTargetId(partnerOrganisation.getId())).thenReturn(currentEligibilityProcess);

        // Set the destination state which we expect when the event is fired
        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_ELIGIBILITY, destinationEligibilityState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_ELIGIBILITY, destinationEligibilityState.getBackingState())).thenReturn(expectedActivityState);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(partnerOrganisation, internalUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected EligibilityProcess object (say X) and verifying that X was the object that was saved.
        EligibilityProcess expectedEligibilityProcess = new EligibilityProcess(internalUser, partnerOrganisation, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedEligibilityProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(eligibilityProcessRepositoryMock).save(expectedEligibilityProcess);
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
        repositories.add(ActivityStateRepository.class);
        return repositories;
    }
}
