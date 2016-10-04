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
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.address.builder.AddressBuilder.newAddress;
import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.project.resource.ProjectDetailsOutcomes.*;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.workflow.domain.ActivityType.PROJECT_SETUP_PROJECT_DETAILS;
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
    public void testAddProjectStartDateAndReadyForSubmission() throws Exception {

        assertAddMandatoryValueAndNowReadyForSubmissionFromPending(
                (project, projectUser) -> projectDetailsWorkflowHandler.projectStartDateAdded(project, projectUser), PROJECT_START_DATE_ADDED);
    }

    @Test
    public void testAddProjectAddress() throws Exception {

        assertAddMandatoryValue((project, projectUser) -> projectDetailsWorkflowHandler.projectAddressAdded(project, projectUser),
                PROJECT_ADDRESS_ADDED);
    }

    @Test
    public void testAddProjectAddressAndReadyForSubmission() throws Exception {

        assertAddMandatoryValueAndNowReadyForSubmissionFromPending(
                (project, projectUser) -> projectDetailsWorkflowHandler.projectAddressAdded(project, projectUser), PROJECT_ADDRESS_ADDED);
    }

    @Test
    public void testAddProjectManager() throws Exception {

        assertAddMandatoryValue((project, projectUser) -> projectDetailsWorkflowHandler.projectManagerAdded(project, projectUser),
                PROJECT_MANAGER_ADDED);
    }

    @Test
    public void testAddProjectManagerAndReadyForSubmission() throws Exception {

        assertAddMandatoryValueAndNowReadyForSubmissionFromPending(
                (project, projectUser) -> projectDetailsWorkflowHandler.projectManagerAdded(project, projectUser), PROJECT_MANAGER_ADDED);
    }

    @Test
    public void testAddProjectFinanceContact() throws Exception {

        assertAddMandatoryValue((project, projectUser) -> projectDetailsWorkflowHandler.projectFinanceContactAdded(project, projectUser),
                PROJECT_FINANCE_CONTACT_ADDED);
    }

    @Test
    public void testAddProjectFinanceContactAndReadyForSubmission() throws Exception {

        assertAddMandatoryValueAndNowReadyForSubmissionFromPending(
                (project, projectUser) -> projectDetailsWorkflowHandler.projectFinanceContactAdded(project, projectUser), PROJECT_FINANCE_CONTACT_ADDED);
    }

    @Test
    public void testAddProjectStartDateAndReadyForSubmissionWhenAlreadyInReadyForSubmission() throws Exception {

        assertAddMandatoryValueAndNowReadyForSubmissionFromReadyToSubmit(
                (project, projectUser) -> projectDetailsWorkflowHandler.projectStartDateAdded(project, projectUser), PROJECT_START_DATE_ADDED);
    }

    @Test
    public void testAddProjectAddressAndReadyForSubmissionWhenAlreadyInReadyForSubmission() throws Exception {

        assertAddMandatoryValueAndNowReadyForSubmissionFromReadyToSubmit(
                (project, projectUser) -> projectDetailsWorkflowHandler.projectAddressAdded(project, projectUser), PROJECT_ADDRESS_ADDED);
    }

    @Test
    public void testAddProjectManagerAndReadyForSubmissionWhenAlreadyInReadyForSubmission() throws Exception {

        assertAddMandatoryValueAndNowReadyForSubmissionFromReadyToSubmit(
                (project, projectUser) -> projectDetailsWorkflowHandler.projectManagerAdded(project, projectUser), PROJECT_MANAGER_ADDED);
    }

    @Test
    public void testAddProjectFinanceContactAndReadyForSubmissionWhenAlreadyInReadyForSubmission() throws Exception {

        assertAddMandatoryValueAndNowReadyForSubmissionFromReadyToSubmit(
                (project, projectUser) -> projectDetailsWorkflowHandler.projectFinanceContactAdded(project, projectUser), PROJECT_FINANCE_CONTACT_ADDED);
    }

    @Test
    public void testSubmissionNotAllowedUntilMandatoryValuesAvailable() {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState pendingState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, State.PENDING);
        ProjectDetailsProcess pendingProcess = new ProjectDetailsProcess(projectUser, project, pendingState);

        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(pendingProcess);

        // now call the method under test
        assertFalse(projectDetailsWorkflowHandler.submitProjectDetails(project, projectUser));

        verify(projectDetailsProcessRepositoryMock, atLeastOnce()).findOneByTargetId(project.getId());

        verify(projectDetailsProcessRepositoryMock, never()).save(any(ProjectDetailsProcess.class));

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void testSubmissionNotAllowedUntilMandatoryValuesAvailableCheckMethod() {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState pendingState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, State.PENDING);
        ProjectDetailsProcess pendingProcess = new ProjectDetailsProcess(projectUser, project, pendingState);

        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(pendingProcess);

        // now call the method under test
        assertFalse(projectDetailsWorkflowHandler.isSubmissionAllowed(project));

        verify(projectDetailsProcessRepositoryMock, atLeastOnce()).findOneByTargetId(project.getId());

        verify(projectDetailsProcessRepositoryMock, never()).save(any(ProjectDetailsProcess.class));

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void testSubmissionNotAllowedUntilMandatoryValuesAvailableButInCorrectState() {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState readyToSubmitState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, State.READY_TO_SUBMIT);
        ProjectDetailsProcess readyToSubmitProcess = new ProjectDetailsProcess(projectUser, project, readyToSubmitState);

        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(readyToSubmitProcess);

        // now call the method under test
        assertTrue(projectDetailsWorkflowHandler.submitProjectDetails(project, projectUser));

        verify(projectDetailsProcessRepositoryMock, atLeastOnce()).findOneByTargetId(project.getId());

        verify(projectDetailsProcessRepositoryMock, never()).save(any(ProjectDetailsProcess.class));

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void testSubmissionAllowedWhenMandatoryValuesAvailable() {

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

        ActivityState readyToSubmitState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, State.READY_TO_SUBMIT);
        ProjectDetailsProcess readyToSubmitProcess = new ProjectDetailsProcess(projectUser, project, readyToSubmitState);

        ActivityState submittedState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, State.SUBMITTED);

        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.SUBMITTED)).thenReturn(submittedState);

        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(readyToSubmitProcess);

        // now call the method under test
        assertTrue(projectDetailsWorkflowHandler.submitProjectDetails(project, projectUser));

        verify(activityStateRepositoryMock, atLeastOnce()).findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.SUBMITTED);

        verify(projectDetailsProcessRepositoryMock, atLeastOnce()).findOneByTargetId(project.getId());

        verify(projectDetailsProcessRepositoryMock).save(processExpectations(project.getId(), projectUser.getId(), ProjectDetailsState.SUBMITTED, SUBMIT));

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void testSubmissionAllowedWhenMandatoryValuesAvailableCheckDoesNotUpdateAnything() {

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

        ActivityState readyToSubmitState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, State.READY_TO_SUBMIT);
        ProjectDetailsProcess readyToSubmitProcess = new ProjectDetailsProcess(projectUser, project, readyToSubmitState);

        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(readyToSubmitProcess);

        // now call the method under test
        assertTrue(projectDetailsWorkflowHandler.isSubmissionAllowed(project));

        verify(projectDetailsProcessRepositoryMock, atLeastOnce()).findOneByTargetId(project.getId());

        verifyNoMoreInteractionsWithMocks();
    }

    /**
     * Test that adding one of the mandatory values prior to being able to submit the project details works and keeps the
     * state in pending until ready to submit
     */
    private void assertAddMandatoryValue(BiFunction<Project, ProjectUser, Boolean> handlerMethod, ProjectDetailsOutcomes expectedEvent) {

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

    private void assertAddMandatoryValueAndNowReadyForSubmissionFromPending(BiFunction<Project, ProjectUser, Boolean> handlerFn, ProjectDetailsOutcomes expectedEvent) {
        assertAddMandatoryValueAndNowReadyForSubmission(ProjectDetailsState.READY_TO_SUBMIT, handlerFn, expectedEvent);
    }

    private void assertAddMandatoryValueAndNowReadyForSubmissionFromReadyToSubmit(BiFunction<Project, ProjectUser, Boolean> handlerFn, ProjectDetailsOutcomes expectedEvent) {
        assertAddMandatoryValueAndNowReadyForSubmission(ProjectDetailsState.PENDING, handlerFn, expectedEvent);
    }

        /**
         * This asserts that triggering the given handler method with a fully filled in Project will move the process into
         * Ready to Submit because all the mandatory values are now provided
         */
    private void assertAddMandatoryValueAndNowReadyForSubmission(ProjectDetailsState originalState, BiFunction<Project, ProjectUser, Boolean> handlerFn, ProjectDetailsOutcomes expectedEvent) {

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

        ActivityState readyToSubmitState = new ActivityState(PROJECT_SETUP_PROJECT_DETAILS, State.READY_TO_SUBMIT);

        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.READY_TO_SUBMIT)).thenReturn(readyToSubmitState);

        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(originalProcess);
        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(updatedProcess);

        // now call the method under test
        assertTrue(handlerFn.apply(project, projectUser));

        verify(activityStateRepositoryMock, atLeastOnce()).findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, State.READY_TO_SUBMIT);

        verify(projectDetailsProcessRepositoryMock, atLeastOnce()).findOneByTargetId(project.getId());

        verify(projectDetailsProcessRepositoryMock).save(processExpectations(project.getId(), projectUser.getId(), ProjectDetailsState.READY_TO_SUBMIT, expectedEvent));

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