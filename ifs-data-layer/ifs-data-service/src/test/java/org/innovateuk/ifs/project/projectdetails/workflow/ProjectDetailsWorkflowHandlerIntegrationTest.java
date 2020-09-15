package org.innovateuk.ifs.project.projectdetails.workflow;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.projectdetails.domain.ProjectDetailsProcess;
import org.innovateuk.ifs.project.projectdetails.repository.ProjectDetailsProcessRepository;
import org.innovateuk.ifs.project.projectdetails.workflow.actions.BaseProjectDetailsAction;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.resource.ProjectDetailsEvent;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.resource.ProjectDetailsEvent.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ProjectDetailsWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<ProjectDetailsWorkflowHandler, ProjectDetailsProcessRepository, BaseProjectDetailsAction> {

    @Autowired
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;
    private ProjectDetailsProcessRepository projectDetailsProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        projectDetailsProcessRepositoryMock = (ProjectDetailsProcessRepository) mockSupplier.apply(ProjectDetailsProcessRepository.class);
    }

    @Test
    public void testProjectCreated() {
        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // this first step will not have access to an existing Process, because it's just starting
        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(null);

        // now call the method under test
        assertTrue(projectDetailsWorkflowHandler.projectCreated(project, projectUser));

        verify(projectDetailsProcessRepositoryMock).findOneByTargetId(project.getId());

        verify(projectDetailsProcessRepositoryMock).save(
                processExpectations(project.getId(), projectUser.getId(), ProjectDetailsState.PENDING, PROJECT_CREATED));

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void projectStartDateAdded() {
        assertAddMandatoryValue((project, projectUser) ->
                        projectDetailsWorkflowHandler.projectStartDateAdded(project, projectUser), PROJECT_START_DATE_ADDED);
    }

    @Test
    public void projectStartDateAddedAndSubmitted() {
        assertAddMandatoryValueAndNowSubmittedFromPending((project, projectUser) ->
                projectDetailsWorkflowHandler.projectStartDateAdded(project, projectUser), PROJECT_START_DATE_ADDED);
    }

    @Test
    public void projectAddressAdded() {
        assertAddMandatoryValue((project, projectUser) ->
                        projectDetailsWorkflowHandler.projectAddressAdded(project, projectUser), PROJECT_ADDRESS_ADDED);
    }

    @Test
    public void projectAddressAddedAndSubmitted() {
        assertAddMandatoryValueAndNowSubmittedFromPending(
                (project, projectUser) -> projectDetailsWorkflowHandler.projectAddressAdded(project, projectUser), PROJECT_ADDRESS_ADDED);
    }

    @Test
    public void projectLocationAdded() {
        assertAddMandatoryValue((project, projectUser)
                        -> projectDetailsWorkflowHandler.projectLocationAdded(project, projectUser), PROJECT_LOCATION_ADDED);
    }

    @Test
    public void projectLocationAddedAndSubmitted() {
        assertAddMandatoryValueAndNowSubmittedFromPending(
                (project, projectUser) -> projectDetailsWorkflowHandler.projectLocationAdded(project, projectUser), PROJECT_LOCATION_ADDED);
    }

    /**
     * Test that adding one of the mandatory values (when other mandatory values are not yet filled in) keeps the
     * state in pending
     */
    private void assertAddMandatoryValue(BiFunction<Project, ProjectUser, Boolean> handlerMethod, ProjectDetailsEvent expectedEvent) {
        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        ProjectDetailsProcess pendingProcess = new ProjectDetailsProcess(projectUser, project, ProjectDetailsState.PENDING);

        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(pendingProcess);

        // now call the method under test
        assertTrue(handlerMethod.apply(project, projectUser));

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
     * 'Submitted' state because all the mandatory values are now provided
     */
    private void assertAddMandatoryValueAndNowSubmitted(ProjectDetailsState originalState, BiFunction<Project, ProjectUser, Boolean> handlerFn, ProjectDetailsEvent expectedEvent) {
        Project project = newProject().
                withApplication(newApplication().withCompetition(newCompetition().withLocationPerPartner(true).build()).build()).
                withTargetStartDate(LocalDate.of(2016, 11, 01)).
                withPartnerOrganisations(newPartnerOrganisation().withOrganisation(newOrganisation().build()).withPostcode("POSTCODE").build(1)).
                withAddress(newAddress().build()).
                build();

        ProjectUser projectUser = newProjectUser().withOrganisation(newOrganisation().build()).build();

        ProjectDetailsProcess originalProcess = new ProjectDetailsProcess(projectUser, project, originalState);
        ProjectDetailsProcess updatedProcess = new ProjectDetailsProcess(projectUser, project, originalState);

        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(originalProcess);
        when(projectDetailsProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(updatedProcess);

        // now call the method under test
        assertTrue(handlerFn.apply(project, projectUser));

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
        assertEquals(expectedState, process.getProcessState());
        assertEquals(expectedEvent.getType(), process.getProcessEvent());
    }

    @Override
    protected Class<BaseProjectDetailsAction> getBaseActionType() {
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