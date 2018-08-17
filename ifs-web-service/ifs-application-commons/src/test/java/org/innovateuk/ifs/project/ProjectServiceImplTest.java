package org.innovateuk.ifs.project;

import junit.framework.TestCase;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.BaseIntegrationTest.setLoggedInUser;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.*;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceImplTest extends BaseServiceUnitTest<ProjectService> {

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private UserService userService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ProjectService projectService;

    @Override
    protected ProjectService supplyServiceUnderTest() { return new ProjectServiceImpl(); }

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

        assertEquals(result.getSuccess(), projects);

        verify(projectRestService).findByUserId(1L);
    }

    @Test
    public void getPartnerOrganisationsForProject() {

        Long projectId = 1L;
        Long organisationId1 = 12L;
        Long organisationId2 = 14L;

        List<PartnerOrganisationResource> partnerOrganisationResources = newPartnerOrganisationResource()
                .withOrganisation(organisationId1, organisationId2)
                .build(2);

        List<OrganisationResource> organisationResources = newOrganisationResource()
                .withId(organisationId1, organisationId2)
                .build(2);

        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(partnerOrganisationResources));
        when(organisationRestService.getOrganisationById(organisationId1)).thenReturn(restSuccess(organisationResources.get(0)));
        when(organisationRestService.getOrganisationById(organisationId2)).thenReturn(restSuccess(organisationResources.get(1)));

        List<OrganisationResource> result = service.getPartnerOrganisationsForProject(projectId);

        assertTrue(result.size() == 2);
        assertEquals(organisationResources.get(0), result.get(0));
        assertEquals(organisationResources.get(1), result.get(1));
    }

    @Test
    public void testGetLeadOrganisation() {

        OrganisationResource organisationResource = newOrganisationResource().build();

        ApplicationResource applicationResource = newApplicationResource().build();

        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).build();

        ProcessRoleResource processRoleResource = newProcessRoleResource()
                .withApplication(applicationResource.getId())
                .withOrganisation(organisationResource.getId())
                .withRole(Role.LEADAPPLICANT)
                .build();

        when(projectRestService.getProjectById(projectResource.getId())).thenReturn(restSuccess(projectResource));

        when(userService.getLeadApplicantProcessRoleOrNull(projectResource.getApplication())).thenReturn(processRoleResource);

        when(organisationRestService.getOrganisationById(processRoleResource.getOrganisationId())).thenReturn(restSuccess(organisationResource));

        OrganisationResource returnedOrganisationResource = service.getLeadOrganisation(projectResource.getId());

        assertEquals(organisationResource, returnedOrganisationResource);

        verify(projectRestService).getProjectById(projectResource.getId());

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

    @Test
    public void testGetPartnerOrganisation() {
        PartnerOrganisationResource partnerOrg = new PartnerOrganisationResource();
        when(projectRestService.getPartnerOrganisation(123L, 234L)).thenReturn(restSuccess(partnerOrg));
        assertTrue(service.getPartnerOrganisation(123L, 234L).equals(partnerOrg));
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testGetPartnerOrganisationNotFound() {
        when(projectRestService.getPartnerOrganisation(123L, 234L)).thenThrow(new ObjectNotFoundException());
        service.getPartnerOrganisation(123L, 234L);
    }

    @Test
    public void testCreateProjectFromApplication() {
        Long applicationId = 2L;
        ProjectResource projectResource = newProjectResource().build();
        when(projectRestService.createProjectFromApplicationId(applicationId)).thenReturn(restSuccess(projectResource));
        assertEquals(service.createProjectFromApplicationId(applicationId).getSuccess(), projectResource);
    }

    @Test
    public void testUserIsPartnerInOrganisationForProject(){
        Long projectId = 1L;
        Long userId = 2L;
        Long expectedOrgId = 3L;

        UserResource userResource = newUserResource().withId(userId).build();

        setLoggedInUser(userResource);

        when(projectService.userIsPartnerInOrganisationForProject(projectId, expectedOrgId, userId)).thenReturn(true);

        boolean result = projectService.userIsPartnerInOrganisationForProject(projectId, expectedOrgId, userId);

        assertTrue(result);
    }

    @Test
    public void testUserIsNotPartnerInOrganisationForProject(){
        Long projectId = 1L;
        Long userId = 2L;
        Long expectedOrgId = 3L;
        Long anotherOrgId = 4L;

        UserResource userResource = newUserResource().withId(userId).build();

        setLoggedInUser(userResource);

        when(projectService.getProjectUsersForProject(projectId)).
                thenReturn(Collections.singletonList(newProjectUserResource().withUser(userId).withOrganisation(anotherOrgId).withRole(Role.PARTNER).build()));

        boolean result = projectService.userIsPartnerInOrganisationForProject(projectId, expectedOrgId, userId);

        TestCase.assertFalse(result);
    }


    @Test
    public void testGetOrganisationIdFromUser() {
        Long projectId = 1L;
        Long userId = 2L;
        Long expectedOrgId = 3L;

        UserResource userResource = newUserResource().withId(userId).build();

        setLoggedInUser(userResource);

        when(projectService.getOrganisationIdFromUser(projectId, userResource)).thenReturn(expectedOrgId);

        Long organisationId = projectService.getOrganisationIdFromUser(projectId, userResource);

        Assert.assertEquals(expectedOrgId, organisationId);
    }

    @Test(expected = ForbiddenActionException.class)
    public void testGetOrganisationIdFromUserThrowsForbiddenException() {
        Long projectId = 1L;
        Long userId = 2L;
        Long expectedOrgId = 3L;

        UserResource userResource = newUserResource().withId(userId).build();

        setLoggedInUser(userResource);

        when(projectService.getProjectUsersForProject(projectId)).thenReturn(emptyList());

        Long organisationId = service.getOrganisationIdFromUser(projectId, userResource);

        Assert.assertEquals(expectedOrgId, organisationId);
    }
}