package org.innovateuk.ifs.project.workflow.projectdetails;

import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.projectdetails.domain.ProjectDetailsProcess;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.projectdetails.repository.ProjectDetailsProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectDetailsEvent;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.innovateuk.ifs.project.projectdetails.workflow.actions.BaseProjectDetailsAction;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.resource.ProjectDetailsEvent.*;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP_PROJECT_DETAILS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectDetailsWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<ProjectDetailsWorkflowHandler, ProjectDetailsProcessRepository, BaseProjectDetailsAction> {

    @Autowired
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;
    private ActivityStateRepository activityStateRepositoryMock;
    private ProjectDetailsProcessRepository projectDetailsProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        projectDetailsProcessRepositoryMock = (ProjectDetailsProcessRepository) mockSupplier.apply(ProjectDetailsProcessRepository.class);
    }

    @Test
    public void testProjectCreated() throws Exception {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState pendingState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, State.PENDING);
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.PENDING)).thenReturn(pendingState);

        // this first step will not have access to an existing Process, because it's just starting
        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(null);

        // now call the method under test
        assertTrue(projectDetailsWorkflowHandler.projectCreated(project, projectUser));

        verify(projectDetailsProcessRepositoryMock).findOneByTargetId(project.getId());

        verify(activityStateRepositoryMock).findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.PENDING);

        verify(projectDetailsProcessRepositoryMock).save(
                processExpectations(project.getId(), projectUser.getId(), ProjectDetailsState.PENDING, PROJECT_CREATED));

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void testAddProjectStartDate() throws Exception {

        assertAddMandatoryValue((project, projectUser) -> projectDetailsWorkflowHandler.projectStartDateAdded(project, projectUser),
                PROJECT_START_DATE_ADDED);
    }

    @Test
    public void testAddProjectStartDateAndSubmitted() throws Exception {

        assertAddMandatoryValueAndNowSubmittedFromPending(
                (project, projectUser) -> projectDetailsWorkflowHandler.projectStartDateAdded(project, projectUser), PROJECT_START_DATE_ADDED);
    }

    @Test
    public void testAddProjectAddress() throws Exception {

        assertAddMandatoryValue((project, projectUser) -> projectDetailsWorkflowHandler.projectAddressAdded(project, projectUser),
                PROJECT_ADDRESS_ADDED);
    }

    @Test
    public void testAddProjectAddressAndSubmitted() throws Exception {

        assertAddMandatoryValueAndNowSubmittedFromPending(
                (project, projectUser) -> projectDetailsWorkflowHandler.projectAddressAdded(project, projectUser), PROJECT_ADDRESS_ADDED);
    }

    @Test
    public void testAddProjectManager() throws Exception {

        assertAddMandatoryValue((project, projectUser) -> projectDetailsWorkflowHandler.projectManagerAdded(project, projectUser),
                PROJECT_MANAGER_ADDED);
    }

    @Test
    public void testAddProjectManagerAndSubmitted() throws Exception {

        assertAddMandatoryValueAndNowSubmittedFromPending(
                (project, projectUser) -> projectDetailsWorkflowHandler.projectManagerAdded(project, projectUser), PROJECT_MANAGER_ADDED);
    }

    /**
     * Test that adding one of the mandatory values prior to being able to submit the project details works and keeps the
     * state in pending until ready to submit
     */
    private void assertAddMandatoryValue(BiFunction<Project, ProjectUser, Boolean> handlerMethod, ProjectDetailsEvent expectedEvent) {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState pendingState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, State.PENDING);
        ProjectDetailsProcess pendingProcess = new ProjectDetailsProcess(projectUser, project, pendingState);

        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.PENDING)).thenReturn(pendingState);

        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(pendingProcess);

        // now call the method under test
        assertTrue(handlerMethod.apply(project, projectUser));

        verify(activityStateRepositoryMock, atLeastOnce()).findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.PENDING);

        verify(projectDetailsProcessRepositoryMock, atLeastOnce()).findOneByTargetId(project.getId());

        verify(projectDetailsProcessRepositoryMock).save(
                processExpectations(project.getId(), projectUser.getId(), ProjectDetailsState.PENDING, expectedEvent));

        verifyNoMoreInteractionsWithMocks();
    }

    private void assertAddMandatoryValueAndNowSubmittedFromPending(BiFunction<Project, ProjectUser, Boolean> handlerFn, ProjectDetailsEvent expectedEvent) {
        assertAddMandatoryValueAndNowSubmitted(ProjectDetailsState.PENDING, handlerFn, expectedEvent);
    }

    /**
     * This asserts that triggering the given handler method with a fully filled in Project will move the process into
     * Submitted state because all the mandatory values are now provided
     */
    private void assertAddMandatoryValueAndNowSubmitted(ProjectDetailsState originalState, BiFunction<Project, ProjectUser, Boolean> handlerFn, ProjectDetailsEvent expectedEvent) {

        List<Organisation> partnerOrgs = newOrganisation().build(2);

        List<ProjectUser> financeContacts = newProjectUser().
                withOrganisation(partnerOrgs.get(0), partnerOrgs.get(1)).
                withRole(PROJECT_FINANCE_CONTACT).
                build(2);

        ProjectUser projectManager = newProjectUser().
                withOrganisation(partnerOrgs.get(0)).
                withRole(PROJECT_MANAGER).
                build();

        Project project = newProject().
                withTargetStartDate(LocalDate.of(2016, 11, 01)).
                withProjectUsers(combineLists(projectManager, financeContacts)).
                withAddress(newAddress().build()).
                build();

        ProjectUser projectUser = newProjectUser().build();

        ActivityState originalActivityState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, originalState.getBackingState());
        ProjectDetailsProcess originalProcess = new ProjectDetailsProcess(projectUser, project, originalActivityState);
        ProjectDetailsProcess updatedProcess = new ProjectDetailsProcess(projectUser, project, originalActivityState);

        ActivityState submittedState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, State.SUBMITTED);

        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.SUBMITTED)).thenReturn(submittedState);

        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(originalProcess);
        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(updatedProcess);

        // now call the method under test
        assertTrue(handlerFn.apply(project, projectUser));

        verify(activityStateRepositoryMock, atLeastOnce()).findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.SUBMITTED);

        verify(projectDetailsProcessRepositoryMock, atLeastOnce()).findOneByTargetId(project.getId());

        verify(projectDetailsProcessRepositoryMock).save(processExpectations(project.getId(), projectUser.getId(), ProjectDetailsState.SUBMITTED, expectedEvent));

        verifyNoMoreInteractionsWithMocks();
    }

    private ProjectDetailsProcess processExpectations(Long expectedProjectId, Long expectedProjectUserId, ProjectDetailsState expectedState, ProjectDetailsEvent expectedEvent) {
        return createLambdaMatcher(process -> {
            assertProcessState(expectedProjectId, expectedProjectUserId, expectedState, expectedEvent, process);
        });
    }

    private void assertProcessState(Long expectedProjectId, Long expectedProjectUserId, ProjectDetailsState expectedState, ProjectDetailsEvent expectedEvent, ProjectDetailsProcess process) {
        assertEquals(expectedProjectId, process.getTarget().getId());
        assertEquals(expectedProjectUserId, process.getParticipant().getId());
        assertEquals(expectedState, process.getActivityState());
        assertEquals(expectedEvent.getType(), process.getProcessEvent());
    }

    @Override
    protected Class getBaseActionType() {
        return BaseProjectDetailsAction.class;
    }

    @Override
    protected Class<ProjectDetailsWorkflowHandler> getWorkflowHandlerType() {
        return ProjectDetailsWorkflowHandler.class;
    }

    @Override
    protected Class<ProjectDetailsProcessRepository> getProcessRepositoryType() {
        return ProjectDetailsProcessRepository.class;
    }
}
