package org.innovateuk.ifs.project.spendprofile.workflow;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.configuration.workflow.SpendProfileWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfileProcess;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileProcessRepository;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileEvent;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileState;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileServiceImplTest;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.ACADEMIC;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.LABOUR;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.MATERIALS;
import static org.innovateuk.ifs.project.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static org.innovateuk.ifs.project.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP_SPEND_PROFILE;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpendProfileWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<SpendProfileWorkflowHandler, SpendProfileProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private SpendProfileWorkflowHandler spendProfileWorkflowHandler;
    private ActivityStateRepository activityStateRepositoryMock;
    private SpendProfileProcessRepository spendProfileProcessRepository;

    @Mock
    private EligibilityWorkflowHandler eligibilityWorkflowHandlerMock;
    @Mock
    private ViabilityWorkflowHandler viabilityWorkflowHandlerMock;

    @Test
    public void testProjectCreated() throws Exception {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, SpendProfileState.PENDING.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_SPEND_PROFILE, SpendProfileState.PENDING.getBackingState())).thenReturn(expectedActivityState);


        // Call the workflow here
        boolean result = spendProfileWorkflowHandler.projectCreated(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state, events etc) are updated in the process table.
        // This can be done by building the expected SpendProfileProcess object (say X) and verifying that X was the object that was saved.
        SpendProfileProcess expectedSpendProfileProcess = new SpendProfileProcess(projectUser, project, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedSpendProfileProcess.setProcessEvent(SpendProfileEvent.PROJECT_CREATED.getType());

        verify(spendProfileProcessRepository).save(expectedSpendProfileProcess);
    }

    @Test
    public void testSpendProfileGenerated() throws Exception {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> spendProfileWorkflowHandler.spendProfileGenerated(project, internalUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.PENDING, SpendProfileState.CREATED, SpendProfileEvent.SPEND_PROFILE_GENERATED);
    }

    @Test
    public void testSpendProfileSubmitted() throws Exception {

        callWorkflowAndCheckTransitionAndEventFiredWithProjectUser(((project, projectUser) -> spendProfileWorkflowHandler.spendProfileSubmitted(project, projectUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.CREATED, SpendProfileState.SUBMITTED, SpendProfileEvent.SPEND_PROFILE_SUBMITTED);
    }

    @Test
    public void testSpendProfileApproved() throws Exception {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> spendProfileWorkflowHandler.spendProfileApproved(project, internalUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.SUBMITTED, SpendProfileState.APPROVED, SpendProfileEvent.SPEND_PROFILE_APPROVED);
    }

    @Test
    public void testSpendProfileRejected() throws Exception {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> spendProfileWorkflowHandler.spendProfileRejected(project, internalUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.SUBMITTED, SpendProfileState.REJECTED, SpendProfileEvent.SPEND_PROFILE_REJECTED);
    }

    @Test
    public void testSpendProfileRejectedIsSubmitted() throws Exception {

        callWorkflowAndCheckTransitionAndEventFiredWithProjectUser(((project, projectUser) -> spendProfileWorkflowHandler.spendProfileSubmitted(project, projectUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.REJECTED, SpendProfileState.SUBMITTED, SpendProfileEvent.SPEND_PROFILE_SUBMITTED);
    }

    @Test
    public void testSubmitSpendProfileWithoutProjectUser() throws Exception {

        callWorkflowAndCheckTransitionAndEventFiredWithoutProjectUser((project -> spendProfileWorkflowHandler.submit(project)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.CREATED, SpendProfileState.SUBMITTED, SpendProfileEvent.SPEND_PROFILE_SUBMITTED);
    }

    @Test
    public void testIsReadyToGenerate() throws Exception {
        SpendProfileWorkflowHandlerIntegrationTest.GenerateSpendProfileData generateSpendProfileData = new SpendProfileWorkflowHandlerIntegrationTest.GenerateSpendProfileData().build();

        Project project = generateSpendProfileData.getProject();

        ActivityState currentActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, SpendProfileState.PENDING.getBackingState());

        SpendProfileProcess currentSpendProfileProcess = new SpendProfileProcess((ProjectUser) null, project, currentActivityState);
        when(spendProfileProcessRepository.findOneByTargetId(project.getId())).thenReturn(currentSpendProfileProcess);

        PartnerOrganisation partnerOrganisation1 = project.getPartnerOrganisations().get(0);
        PartnerOrganisation partnerOrganisation2 = project.getPartnerOrganisations().get(1);
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation2)).thenReturn(ViabilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation1)).thenReturn(EligibilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation2)).thenReturn(EligibilityState.APPROVED);

        ServiceResult<Void> result = spendProfileWorkflowHandler.isReadyToGenerate(project);
        assertTrue(result.isSuccess());
    }

    private void callWorkflowAndCheckTransitionAndEventFiredInternalUser(BiFunction<Project, User, Boolean> workflowMethodToCall, SpendProfileState currentSpendProfileState, SpendProfileState destinationSpendProfileState, SpendProfileEvent expectedEventToBeFired) {

        Project project = newProject().build();
        User internalUser = newUser().build();

        // Set the current state in the Spend Profile Process
        ActivityState currentActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, currentSpendProfileState.getBackingState());
        SpendProfileProcess currentSpendProfileProcess = new SpendProfileProcess((ProjectUser) null, project, currentActivityState);
        when(spendProfileProcessRepository.findOneByTargetId(project.getId())).thenReturn(currentSpendProfileProcess);

        // Set the destination state which we expect when the event is fired
        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, destinationSpendProfileState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_SPEND_PROFILE, destinationSpendProfileState.getBackingState())).thenReturn(expectedActivityState);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project, internalUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected SpendProfileProcess object (say X) and verifying that X was the object that was saved.
        SpendProfileProcess expectedSpendProfileProcess = new SpendProfileProcess(internalUser, project, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedSpendProfileProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(spendProfileProcessRepository).save(expectedSpendProfileProcess);
    }

    private void callWorkflowAndCheckTransitionAndEventFiredWithoutProjectUser(Function<Project, Boolean> workflowMethodToCall, SpendProfileState currentSpendProfileState, SpendProfileState destinationSpendProfileState, SpendProfileEvent expectedEventToBeFired) {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Set the current state in the Spend Profile Process
        ActivityState currentActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, currentSpendProfileState.getBackingState());
        SpendProfileProcess currentSpendProfileProcess = new SpendProfileProcess((ProjectUser) null, project, currentActivityState);
        currentSpendProfileProcess.setParticipant(projectUser);
        when(spendProfileProcessRepository.findOneByTargetId(project.getId())).thenReturn(currentSpendProfileProcess);

        // Set the destination state which we expect when the event is fired
        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, destinationSpendProfileState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_SPEND_PROFILE, destinationSpendProfileState.getBackingState())).thenReturn(expectedActivityState);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected SpendProfileProcess object (say X) and verifying that X was the object that was saved.
        SpendProfileProcess expectedSpendProfileProcess = new SpendProfileProcess(projectUser, project, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedSpendProfileProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(spendProfileProcessRepository).save(expectedSpendProfileProcess);
    }

    private void callWorkflowAndCheckTransitionAndEventFiredWithProjectUser(BiFunction<Project, ProjectUser, Boolean> workflowMethodToCall, SpendProfileState currentSpendProfileState, SpendProfileState destinationSpendProfileState, SpendProfileEvent expectedEventToBeFired) {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Set the current state in the Spend Profile Process
        ActivityState currentActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, currentSpendProfileState.getBackingState());
        SpendProfileProcess currentSpendProfileProcess = new SpendProfileProcess(projectUser, project, currentActivityState);
        currentSpendProfileProcess.setParticipant(projectUser);
        when(spendProfileProcessRepository.findOneByTargetId(project.getId())).thenReturn(currentSpendProfileProcess);

        // Set the destination state which we expect when the event is fired
        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, destinationSpendProfileState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_SPEND_PROFILE, destinationSpendProfileState.getBackingState())).thenReturn(expectedActivityState);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected SpendProfileProcess object (say X) and verifying that X was the object that was saved.
        SpendProfileProcess expectedSpendProfileProcess = new SpendProfileProcess(projectUser, project, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedSpendProfileProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(spendProfileProcessRepository).save(expectedSpendProfileProcess);
    }

    private class GenerateSpendProfileData {

        private Project project;
        private Organisation organisation1;
        private Organisation organisation2;
        private CostCategoryType costCategoryType1;
        private CostCategoryType costCategoryType2;
        private CostCategory type1Cat1;
        private CostCategory type1Cat2;
        private CostCategory type2Cat1;
        private User user;

        public Project getProject() {
            return project;
        }

        public Organisation getOrganisation1() {
            return organisation1;
        }

        public Organisation getOrganisation2() {
            return organisation2;
        }

        public CostCategoryType getCostCategoryType1() {
            return costCategoryType1;
        }

        public CostCategoryType getCostCategoryType2() {
            return costCategoryType2;
        }

        public User getUser() {
            return user;
        }

        public SpendProfileWorkflowHandlerIntegrationTest.GenerateSpendProfileData build() {

            UserResource loggedInUser = newUserResource().build();
            user = newUser().withId(loggedInUser.getId()).build();
            setLoggedInUser(loggedInUser);
            when(userRepositoryMock.findOne(loggedInUser.getId())).thenReturn(user);

            organisation1 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
            organisation2 = newOrganisation().withOrganisationType(OrganisationTypeEnum.RTO).build();

            PartnerOrganisation partnerOrganisation1 = newPartnerOrganisation().withOrganisation(organisation1).build();
            PartnerOrganisation partnerOrganisation2 = newPartnerOrganisation().withOrganisation(organisation2).build();

            project = newProject().
                    withId(projectId).
                    withDuration(3L).
                    withPartnerOrganisations(asList(partnerOrganisation1, partnerOrganisation2)).
                    build();

            // First cost category type and everything that goes with it.
            type1Cat1 = newCostCategory().withName(LABOUR.getName()).build();
            type1Cat2 = newCostCategory().withName(MATERIALS.getName()).build();

            costCategoryType1 = newCostCategoryType()
                    .withName("Type 1")
                    .withCostCategoryGroup(
                            newCostCategoryGroup()
                                    .withDescription("Group 1")
                                    .withCostCategories(asList(type1Cat1, type1Cat2))
                                    .build())
                    .build();

            // Second cost category type and everything that goes with it.
            type2Cat1 = newCostCategory().withName(ACADEMIC.getName()).build();

            costCategoryType2 = newCostCategoryType()
                    .withName("Type 2")
                    .withCostCategoryGroup(
                            newCostCategoryGroup()
                                    .withDescription("Group 2")
                                    .withCostCategories(asList(type2Cat1))
                                    .build())
                    .build();

            // set basic repository lookup expectations
            when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
            when(organisationRepositoryMock.findOne(organisation1.getId())).thenReturn(organisation1);
            when(organisationRepositoryMock.findOne(organisation2.getId())).thenReturn(organisation2);
            when(costCategoryRepositoryMock.findOne(type1Cat1.getId())).thenReturn(type1Cat1);
            when(costCategoryRepositoryMock.findOne(type1Cat2.getId())).thenReturn(type1Cat2);
            when(costCategoryRepositoryMock.findOne(type2Cat1.getId())).thenReturn(type2Cat1);
            when(costCategoryTypeRepositoryMock.findOne(costCategoryType1.getId())).thenReturn(costCategoryType1);
            when(costCategoryTypeRepositoryMock.findOne(costCategoryType2.getId())).thenReturn(costCategoryType2);
            return this;
        }
    }

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        spendProfileProcessRepository = (SpendProfileProcessRepository) mockSupplier.apply(SpendProfileProcessRepository.class);
    }

    @Override
    protected Class getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<SpendProfileWorkflowHandler> getWorkflowHandlerType() {
        return SpendProfileWorkflowHandler.class;
    }

    @Override
    protected Class<SpendProfileProcessRepository> getProcessRepositoryType() {
        return SpendProfileProcessRepository.class;
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(SpendProfileProcessRepository.class);
        repositories.add(ActivityStateRepository.class);
        return repositories;
    }
}
