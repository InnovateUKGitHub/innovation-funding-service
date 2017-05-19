package org.innovateuk.ifs.project.service;

import java.util.List;
import java.util.Optional;

import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.project.ProjectServiceImpl;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceImplTest {

    @InjectMocks
    private ProjectServiceImpl service;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ApplicationService applicationService;

    @Test
    public void testGetById() {
        ProjectResource projectResource = newProjectResource().build();

        when(projectRestService.getProjectById(projectResource.getId())).thenReturn(restSuccess(projectResource));

        ProjectResource returnedProjectResource = service.getById(projectResource.getId());

        assertEquals(projectResource, returnedProjectResource);

        verify(projectRestService).getProjectById(projectResource.getId());
    }

    @Test
    public void testGetProjectUsersForProject() {
        Long projectId = 1L;

        List<ProjectUserResource> projectUsers = newProjectUserResource().build(5);

        when(projectRestService.getProjectUsersForProject(projectId)).thenReturn(restSuccess(projectUsers));

        List<ProjectUserResource> returnedProjectUsers = service.getProjectUsersForProject(projectId);

        assertEquals(returnedProjectUsers, projectUsers);

        verify(projectRestService).getProjectUsersForProject(projectId);
    }

    @Test
    public void testGetByApplicationId() {
        ApplicationResource applicationResource = newApplicationResource().build();

        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).build();

        when(projectRestService.getByApplicationId(applicationResource.getId())).thenReturn(restSuccess(projectResource));

        ProjectResource returnedProjectResource = service.getByApplicationId(applicationResource.getId());

        assertEquals(returnedProjectResource, projectResource);

        verify(projectRestService).getByApplicationId(applicationResource.getId());
    }

    @Test
    public void testFindByUser() {
        List<ProjectResource> projects = newProjectResource().build(3);

        when(projectRestService.findByUserId(1L)).thenReturn(restSuccess(projects));

        ServiceResult<List<ProjectResource>> result = service.findByUser(1L);

        assertTrue(result.isSuccess());

        assertEquals(result.getSuccessObject(), projects);

        verify(projectRestService).findByUserId(1L);
    }

    @Test
    public void testGetLeadOrganisation() {
        OrganisationResource organisationResource = newOrganisationResource().build();

        ApplicationResource applicationResource = newApplicationResource().build();

        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).build();

        when(projectRestService.getProjectById(projectResource.getId())).thenReturn(restSuccess(projectResource));

        when(applicationService.getLeadOrganisation(projectResource.getApplication())).thenReturn(organisationResource);

        OrganisationResource returnedOrganisationResource = service.getLeadOrganisation(projectResource.getId());

        assertEquals(organisationResource, returnedOrganisationResource);

        verify(projectRestService).getProjectById(projectResource.getId());

        verify(applicationService).getLeadOrganisation(projectResource.getApplication());
    }

    @Test
    public void testGetProjectTeamStatus() throws Exception {
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().build();

        when(projectRestService.getProjectTeamStatus(1L, Optional.empty())).thenReturn(restSuccess(expectedProjectTeamStatusResource));

        ProjectTeamStatusResource projectTeamStatusResource = service.getProjectTeamStatus(1L, Optional.empty());

        assertEquals(expectedProjectTeamStatusResource, projectTeamStatusResource);

        verify(projectRestService).getProjectTeamStatus(1L, Optional.empty());
    }

    @Test
    public void testGetProjectTeamStatusWithFilterByUserId() throws Exception {
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().build();

        when(projectRestService.getProjectTeamStatus(1L, Optional.of(456L))).thenReturn(restSuccess(expectedProjectTeamStatusResource));

        ProjectTeamStatusResource projectTeamStatusResource = service.getProjectTeamStatus(1L, Optional.of(456L));

        assertEquals(expectedProjectTeamStatusResource, projectTeamStatusResource);

        verify(projectRestService).getProjectTeamStatus(1L, Optional.of(456L));
    }

    @Test
    public void testGetProjectManager() {
        Long projectId = 123L;
        final Long projectManagerId = 987L;
        when(projectRestService.getProjectManager(projectId))
                .thenReturn(restSuccess(newProjectUserResource().withUser(projectManagerId).build()));
        assertTrue(service.getProjectManager(projectId).isPresent());
    }

    @Test
    public void testGetProjectManagerWhenNotFound() {
        Long projectId = 123L;
        final Long projectManagerId = 987L;
        when(projectRestService.getProjectManager(projectId))
                .thenReturn(restSuccess(null, HttpStatus.NOT_FOUND));
        assertFalse(service.getProjectManager(projectId).isPresent());
    }

    @Test
    public void testIsProjectManager() {
        final Long projectId = 123L;
        final Long projectManagerId = 987L;

        when(projectRestService.getProjectManager(projectId))
                .thenReturn(restSuccess(newProjectUserResource().withUser(projectManagerId).build()));
        assertTrue(service.isProjectManager(projectManagerId, projectId));
    }

    @Test
    public void testIsProjectManagerWhenNotFound() {
        final Long projectId = 123L;
        final Long projectManagerId = 987L;

        when(projectRestService.getProjectManager(projectId)).thenReturn(restSuccess(null, HttpStatus.NOT_FOUND));

        final Optional<ProjectUserResource> projectManager = service.getProjectManager(projectId);
        assertFalse(service.isProjectManager(projectManagerId, projectId));
    }

    @Test
    public void testIsProjectManagerWhenNotIt() {
        final Long projectId = 123L;
        final Long projectManagerId = 987L;
        final Long loggedInUserId = 742L;

        when(projectRestService.getProjectManager(projectId))
                .thenReturn(restSuccess(newProjectUserResource().withUser(projectManagerId).build()));

        assertFalse(service.isProjectManager(loggedInUserId, projectId));
    }
}