package org.innovateuk.ifs.project.spendprofile.workflow;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.financechecks.repository.EligibilityProcessRepository;
import org.innovateuk.ifs.project.financechecks.repository.ViabilityProcessRepository;
import org.innovateuk.ifs.project.spendprofile.configuration.workflow.SpendProfileWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfileProcess;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileProcessRepository;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileEvent;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpendProfileWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<SpendProfileWorkflowHandler, SpendProfileProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private SpendProfileWorkflowHandler spendProfileWorkflowHandler;
    private SpendProfileProcessRepository spendProfileProcessRepository;

    @Mock
    protected UserRepository userRepositoryMock;
    @Mock
    protected ProjectRepository projectRepositoryMock;
    @Mock
    protected OrganisationRepository organisationRepositoryMock;

    @Test
    public void projectCreated() {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Call the workflow here
        boolean result = spendProfileWorkflowHandler.projectCreated(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state, events etc) are updated in the process table.
        // This can be done by building the expected SpendProfileProcess object (say X) and verifying that X was the object that was saved.
        SpendProfileProcess expectedSpendProfileProcess = new SpendProfileProcess(projectUser, project, SpendProfileState.PENDING);

        // Ensure the correct event was fired by the workflow
        expectedSpendProfileProcess.setProcessEvent(SpendProfileEvent.PROJECT_CREATED.getType());

        verify(spendProfileProcessRepository).save(expectedSpendProfileProcess);
    }

    @Test
    public void spendProfileGenerated() {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> spendProfileWorkflowHandler.spendProfileGenerated(project, internalUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.PENDING, SpendProfileState.CREATED, SpendProfileEvent.SPEND_PROFILE_GENERATED);
    }

    @Test
    public void spendProfileSubmitted() {

        callWorkflowAndCheckTransitionAndEventFiredWithProjectUser(((project, projectUser) -> spendProfileWorkflowHandler.spendProfileSubmitted(project, projectUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.CREATED, SpendProfileState.SUBMITTED, SpendProfileEvent.SPEND_PROFILE_SUBMITTED);
    }

    @Test
    public void spendProfileApproved() {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> spendProfileWorkflowHandler.spendProfileApproved(project, internalUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.SUBMITTED, SpendProfileState.APPROVED, SpendProfileEvent.SPEND_PROFILE_APPROVED);
    }

    @Test
    public void spendProfileRejected() {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> spendProfileWorkflowHandler.spendProfileRejected(project, internalUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.SUBMITTED, SpendProfileState.REJECTED, SpendProfileEvent.SPEND_PROFILE_REJECTED);
    }

    @Test
    public void spendProfileRejectedIsSubmitted() {

        callWorkflowAndCheckTransitionAndEventFiredWithProjectUser(((project, projectUser) -> spendProfileWorkflowHandler.spendProfileSubmitted(project, projectUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.REJECTED, SpendProfileState.SUBMITTED, SpendProfileEvent.SPEND_PROFILE_SUBMITTED);
    }

    @Test
    public void submitSpendProfileWithoutProjectUser() {

        callWorkflowAndCheckTransitionAndEventFiredWithoutProjectUser((project -> spendProfileWorkflowHandler.submit(project)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.CREATED, SpendProfileState.SUBMITTED, SpendProfileEvent.SPEND_PROFILE_SUBMITTED);
    }

    @Test
    public void isReadyToGenerate() {
        SpendProfileWorkflowHandlerIntegrationTest.GenerateSpendProfileData generateSpendProfileData = new SpendProfileWorkflowHandlerIntegrationTest.GenerateSpendProfileData().build();

        Project project = generateSpendProfileData.getProject();

        SpendProfileProcess currentSpendProfileProcess = new SpendProfileProcess((ProjectUser) null, project, SpendProfileState.PENDING);
        when(spendProfileProcessRepository.findOneByTargetId(project.getId())).thenReturn(currentSpendProfileProcess);

        assertFalse(spendProfileWorkflowHandler.isAlreadyGenerated(project));
    }

    @Test
    public void isAlreadyGenerated() {
        SpendProfileWorkflowHandlerIntegrationTest.GenerateSpendProfileData generateSpendProfileData = new SpendProfileWorkflowHandlerIntegrationTest.GenerateSpendProfileData().build();

        Project project = generateSpendProfileData.getProject();

        SpendProfileProcess currentSpendProfileProcess = new SpendProfileProcess((ProjectUser) null, project, SpendProfileState.CREATED);
        when(spendProfileProcessRepository.findOneByTargetId(project.getId())).thenReturn(currentSpendProfileProcess);

        assertTrue(spendProfileWorkflowHandler.isAlreadyGenerated(project));
    }

    @Test
    public void projectHasNoPendingPartners() {
        SpendProfileWorkflowHandlerIntegrationTest.GenerateSpendProfileData generateSpendProfileData = new SpendProfileWorkflowHandlerIntegrationTest.GenerateSpendProfileData().build();

        Project project = generateSpendProfileData.getProject();

        SpendProfileProcess currentSpendProfileProcess = new SpendProfileProcess((ProjectUser) null, project, SpendProfileState.CREATED);
        when(spendProfileProcessRepository.findOneByTargetId(project.getId())).thenReturn(currentSpendProfileProcess);

        assertTrue(spendProfileWorkflowHandler.projectHasNoPendingPartners(project));
    }

    private void callWorkflowAndCheckTransitionAndEventFiredInternalUser(BiFunction<Project, User, Boolean> workflowMethodToCall, SpendProfileState currentSpendProfileState, SpendProfileState destinationSpendProfileState, SpendProfileEvent expectedEventToBeFired) {

        Project project = newProject().build();
        User internalUser = newUser().build();

        // Set the current state in the Spend Profile Process
        SpendProfileProcess currentSpendProfileProcess = new SpendProfileProcess((ProjectUser) null, project, currentSpendProfileState);
        when(spendProfileProcessRepository.findOneByTargetId(project.getId())).thenReturn(currentSpendProfileProcess);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project, internalUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected SpendProfileProcess object (say X) and verifying that X was the object that was saved.
        SpendProfileProcess expectedSpendProfileProcess = new SpendProfileProcess(internalUser, project, destinationSpendProfileState);

        // Ensure the correct event was fired by the workflow
        expectedSpendProfileProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(spendProfileProcessRepository).save(expectedSpendProfileProcess);
    }

    private void callWorkflowAndCheckTransitionAndEventFiredWithoutProjectUser(Function<Project, Boolean> workflowMethodToCall, SpendProfileState currentSpendProfileState, SpendProfileState destinationSpendProfileState, SpendProfileEvent expectedEventToBeFired) {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Set the current state in the Spend Profile Process
        SpendProfileProcess currentSpendProfileProcess = new SpendProfileProcess((ProjectUser) null, project, currentSpendProfileState);
        currentSpendProfileProcess.setParticipant(projectUser);
        when(spendProfileProcessRepository.findOneByTargetId(project.getId())).thenReturn(currentSpendProfileProcess);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected SpendProfileProcess object (say X) and verifying that X was the object that was saved.
        SpendProfileProcess expectedSpendProfileProcess = new SpendProfileProcess(projectUser, project, destinationSpendProfileState);

        // Ensure the correct event was fired by the workflow
        expectedSpendProfileProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(spendProfileProcessRepository).save(expectedSpendProfileProcess);
    }

    private void callWorkflowAndCheckTransitionAndEventFiredWithProjectUser(BiFunction<Project, ProjectUser, Boolean> workflowMethodToCall, SpendProfileState currentSpendProfileState, SpendProfileState destinationSpendProfileState, SpendProfileEvent expectedEventToBeFired) {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Set the current state in the Spend Profile Process
        SpendProfileProcess currentSpendProfileProcess = new SpendProfileProcess(projectUser, project, currentSpendProfileState);
        currentSpendProfileProcess.setParticipant(projectUser);
        when(spendProfileProcessRepository.findOneByTargetId(project.getId())).thenReturn(currentSpendProfileProcess);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected SpendProfileProcess object (say X) and verifying that X was the object that was saved.
        SpendProfileProcess expectedSpendProfileProcess = new SpendProfileProcess(projectUser, project, destinationSpendProfileState);

        // Ensure the correct event was fired by the workflow
        expectedSpendProfileProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(spendProfileProcessRepository).save(expectedSpendProfileProcess);
    }

    private class GenerateSpendProfileData {

        private Project project;
        private Organisation organisation1;
        private Organisation organisation2;
        private User user;

        public Project getProject() {
            return project;
        }

        public User getUser() {
            return user;
        }

        public SpendProfileWorkflowHandlerIntegrationTest.GenerateSpendProfileData build() {

            UserResource loggedInUser = newUserResource().build();
            user = newUser().withId(loggedInUser.getId()).build();
            setLoggedInUser(loggedInUser);
            when(userRepositoryMock.findById(loggedInUser.getId())).thenReturn(Optional.of(user));

            organisation1 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
            organisation2 = newOrganisation().withOrganisationType(OrganisationTypeEnum.RTO).build();

            PartnerOrganisation partnerOrganisation1 = newPartnerOrganisation().withOrganisation(organisation1).build();
            PartnerOrganisation partnerOrganisation2 = newPartnerOrganisation().withOrganisation(organisation2).build();

            long projectId = 123L;
            project = newProject().
                    withId(projectId).
                    withDuration(3L).
                    withPartnerOrganisations(asList(partnerOrganisation1, partnerOrganisation2)).
                    build();

            // set basic repository lookup expectations
            when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));
            when(organisationRepositoryMock.findById(organisation1.getId())).thenReturn(Optional.of(organisation1));
            when(organisationRepositoryMock.findById(organisation2.getId())).thenReturn(Optional.of(organisation2));
            return this;
        }
    }

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
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
        repositories.add(EligibilityProcessRepository.class);
        repositories.add(ViabilityProcessRepository.class);
        return repositories;
    }
}