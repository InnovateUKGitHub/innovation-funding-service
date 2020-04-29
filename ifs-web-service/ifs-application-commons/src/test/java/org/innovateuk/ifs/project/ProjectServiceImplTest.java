package org.innovateuk.ifs.project;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.BaseIntegrationTest.setLoggedInUser;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
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
    public void getById() {
        ProjectResource projectResource = newProjectResource().build();

        when(projectRestService.getProjectById(projectResource.getId())).thenReturn(restSuccess(projectResource));

        ProjectResource returnedProjectResource = service.getById(projectResource.getId());

        assertEquals(projectResource, returnedProjectResource);

        verify(projectRestService).getProjectById(projectResource.getId());
    }

    @Test
    public void getProjectUsersForProject() {
        long projectId = 1;

        List<ProjectUserResource> projectUsers = newProjectUserResource().build(5);

        when(projectRestService.getProjectUsersForProject(projectId)).thenReturn(restSuccess(projectUsers));

        List<ProjectUserResource> returnedProjectUsers = service.getProjectUsersForProject(projectId);

        assertEquals(returnedProjectUsers, projectUsers);

        verify(projectRestService).getProjectUsersForProject(projectId);
    }

    @Test
    public void getByApplicationId() {
        ApplicationResource applicationResource = newApplicationResource().build();

        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).build();

        when(projectRestService.getByApplicationId(applicationResource.getId())).thenReturn(restSuccess(projectResource));

        ProjectResource returnedProjectResource = service.getByApplicationId(applicationResource.getId());

        assertEquals(returnedProjectResource, projectResource);

        verify(projectRestService).getByApplicationId(applicationResource.getId());
    }

    @Test
    public void getLeadOrganisation() {
        OrganisationResource organisationResource = newOrganisationResource().build();
        ApplicationResource applicationResource = newApplicationResource().build();
        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).build();

        when(projectRestService.getLeadOrganisationByProject(projectResource.getId())).thenReturn(restSuccess(organisationResource));

        OrganisationResource returnedOrganisationResource = service.getLeadOrganisation(projectResource.getId());

        assertEquals(organisationResource, returnedOrganisationResource);

        verify(projectRestService).getLeadOrganisationByProject(projectResource.getId());
    }

    @Test
    public void getPartnerOrganisationsForProject() {
        long projectId = 1;
        long organisationId1 = 12;
        long organisationId2 = 14;

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

        assertEquals(2, result.size());
        assertEquals(organisationResources.get(0), result.get(0));
        assertEquals(organisationResources.get(1), result.get(1));
    }

    @Test
    public void getProjectManager() {
        long projectId = 123;
        long projectManagerId = 987;

        when(projectRestService.getProjectManager(projectId))
                .thenReturn(restSuccess(newProjectUserResource().withUser(projectManagerId).build()));
        assertTrue(service.getProjectManager(projectId).isPresent());
    }

    @Test
    public void getProjectManager_notFound() {
        long projectId = 123;

        when(projectRestService.getProjectManager(projectId))
                .thenReturn(restSuccess(null, HttpStatus.NOT_FOUND));

        assertFalse(service.getProjectManager(projectId).isPresent());
    }

    @Test
    public void isProjectManager() {
        final long projectId = 123;
        final long projectManagerId = 987;

        when(projectRestService.getProjectManager(projectId))
                .thenReturn(restSuccess(newProjectUserResource().withUser(projectManagerId).build()));
        assertTrue(service.isProjectManager(projectManagerId, projectId));
    }

    @Test
    public void isProjectManager_notFound() {
        final long projectId = 123L;
        final long projectManagerId = 987L;

        when(projectRestService.getProjectManager(projectId)).thenReturn(restSuccess(null, HttpStatus.NOT_FOUND));

        assertFalse(service.isProjectManager(projectManagerId, projectId));
    }

    @Test
    public void isProjectManager_notUser() {
        final long projectId = 123;
        final long projectManagerId = 987;
        final long loggedInUserId = 742;

        when(projectRestService.getProjectManager(projectId))
                .thenReturn(restSuccess(newProjectUserResource().withUser(projectManagerId).build()));

        assertFalse(service.isProjectManager(loggedInUserId, projectId));
    }

    @Test
    public void userIsPartnerInOrganisationForProject(){
        long projectId = 1;
        long userId = 2;
        long expectedOrgId = 3;
        UserResource userResource = newUserResource().withId(userId).build();

        setLoggedInUser(userResource);

        when(projectService.userIsPartnerInOrganisationForProject(projectId, expectedOrgId, userId)).thenReturn(true);

        boolean result = projectService.userIsPartnerInOrganisationForProject(projectId, expectedOrgId, userId);

        assertTrue(result);
    }

    @Test
    public void userIsPartnerInOrganisationForProject_notPartner(){
        long projectId = 1;
        long userId = 2;
        long expectedOrgId = 3;
        long anotherOrgId = 4;
        UserResource userResource = newUserResource().withId(userId).build();

        setLoggedInUser(userResource);

        when(projectService.getProjectUsersForProject(projectId)).
                thenReturn(Collections.singletonList(newProjectUserResource().withUser(userId).withOrganisation(anotherOrgId).withRole(Role.PARTNER).build()));

        boolean result = projectService.userIsPartnerInOrganisationForProject(projectId, expectedOrgId, userId);

        assertFalse(result);
    }

    @Test
    public void getOrganisationIdFromUser() {
        long projectId = 1;
        long userId = 2;
        long expectedOrgId = 3;
        UserResource userResource = newUserResource().withId(userId).build();

        setLoggedInUser(userResource);

        when(projectService.getOrganisationIdFromUser(projectId, userResource)).thenReturn(expectedOrgId);

        long organisationId = projectService.getOrganisationIdFromUser(projectId, userResource);

        assertEquals(expectedOrgId, organisationId);
    }

}