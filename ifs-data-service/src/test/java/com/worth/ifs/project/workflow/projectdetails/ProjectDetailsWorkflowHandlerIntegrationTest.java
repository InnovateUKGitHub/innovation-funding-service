package com.worth.ifs.project.workflow.projectdetails;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.repository.ProjectDetailsProcessRepository;
import com.worth.ifs.project.resource.ProjectDetailsOutcomes;
import com.worth.ifs.project.resource.ProjectDetailsState;
import com.worth.ifs.project.workflow.projectdetails.actions.BaseProjectDetailsAction;
import com.worth.ifs.project.workflow.projectdetails.configuration.ProjectDetailsWorkflowHandler;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import com.worth.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.address.builder.AddressBuilder.newAddress;
import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.project.resource.ProjectDetailsOutcomes.PROJECT_CREATED;
import static com.worth.ifs.project.resource.ProjectDetailsOutcomes.PROJECT_START_DATE_ADDED;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.workflow.domain.ActivityType.PROJECT_SETUP_PROJECT_DETAILS;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState pendingState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, State.PENDING);
        ProjectDetailsProcess pendingProcess = new ProjectDetailsProcess(projectUser, project, pendingState);

        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.PENDING)).thenReturn(pendingState);

        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(pendingProcess);

        // now call the method under test
        assertTrue(projectDetailsWorkflowHandler.projectStartDateAdded(project, projectUser));

        verify(activityStateRepositoryMock, atLeastOnce()).findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.PENDING);

        verify(projectDetailsProcessRepositoryMock, atLeastOnce()).findOneByTargetId(project.getId());

        verify(projectDetailsProcessRepositoryMock).save(
                processExpectations(project.getId(), projectUser.getId(), ProjectDetailsState.PENDING, PROJECT_START_DATE_ADDED));

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void testAddProjectStartDateAndReadyForSubmission() throws Exception {

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

        ActivityState pendingState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, State.PENDING);
        ProjectDetailsProcess pendingProcess = new ProjectDetailsProcess(projectUser, project, pendingState);
        ProjectDetailsProcess updatedProcess = new ProjectDetailsProcess(projectUser, project, pendingState);

        ActivityState readyToSubmitState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, State.READY_TO_SUBMIT);

        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.READY_TO_SUBMIT)).thenReturn(readyToSubmitState);

        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(pendingProcess);
        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(updatedProcess);

        // now call the method under test
        assertTrue(projectDetailsWorkflowHandler.projectStartDateAdded(project, projectUser));

        verify(activityStateRepositoryMock, atLeastOnce()).findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.READY_TO_SUBMIT);

        verify(projectDetailsProcessRepositoryMock, atLeastOnce()).findOneByTargetId(project.getId());

        verify(projectDetailsProcessRepositoryMock).save(processExpectations(project.getId(), projectUser.getId(), ProjectDetailsState.READY_TO_SUBMIT, PROJECT_START_DATE_ADDED));

        verifyNoMoreInteractionsWithMocks();
    }

    private ProjectDetailsProcess processExpectations(Long expectedProjectId, Long expectedProjectUserId, ProjectDetailsState expectedState, ProjectDetailsOutcomes expectedEvent) {
        return createLambdaMatcher(process -> {
            assertProcessState(expectedProjectId, expectedProjectUserId, expectedState, expectedEvent, process);
        });
    }

    private void assertProcessState(Long expectedProjectId, Long expectedProjectUserId, ProjectDetailsState expectedState, ProjectDetailsOutcomes expectedEvent, ProjectDetailsProcess process) {
        assertEquals(expectedProjectId, process.getTarget().getId());
        assertEquals(expectedProjectUserId, process.getParticipant().getId());
        assertEquals(expectedState, process.getActivityState());
        assertEquals(expectedEvent.getType(), process.getProcessEvent());
    }

    private void verifyNoMoreInteractionsWithMocks() {
        verifyNoMoreInteractions(activityStateRepositoryMock, projectDetailsProcessRepositoryMock);
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

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        return asList(ActivityStateRepository.class, ProjectDetailsProcessRepository.class);
    }
}