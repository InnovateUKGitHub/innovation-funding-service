package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectUserResourceList;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class ProjectRestServiceImplTest extends BaseRestServiceUnitTest<ProjectRestServiceImpl> {
    private static final String projectRestURL = "/project";

    @Override
    protected ProjectRestServiceImpl registerRestServiceUnderTest() {
        ProjectRestServiceImpl projectService = new ProjectRestServiceImpl();
        ReflectionTestUtils.setField(projectService, "projectRestURL", projectRestURL);
        return projectService;
    }

    @Test
    public void testGetProjectById() {
        ProjectResource returnedResponse = new ProjectResource();
        setupGetWithRestResultExpectations(projectRestURL + "/123", ProjectResource.class, returnedResponse);
        ProjectResource result = service.getProjectById(123L).getSuccess();
        assertEquals(returnedResponse, result);
    }

    @Test
    public void testGetProjectUsers() {
        List<ProjectUserResource> users = Stream.of(1,2,3).map(i -> new ProjectUserResource()).collect(Collectors.toList());
        setupGetWithRestResultExpectations(projectRestURL + "/123/project-users", projectUserResourceList(), users);
        RestResult<List<ProjectUserResource>> result = service.getProjectUsersForProject(123L);
        assertTrue(result.isSuccess());
        assertEquals(users, result.getSuccess());
    }

    @Test
    public void testFindByUserId() {

        List<ProjectResource> projects = Stream.of(1,2,3).map(i -> new ProjectResource()).collect(Collectors.toList());

        setupGetWithRestResultExpectations(projectRestURL + "/user/" + 1L, projectResourceListType(), projects);

        RestResult<List<ProjectResource>> result = service.findByUserId(1L);

        assertTrue(result.isSuccess());

        assertEquals(projects, result.getSuccess());

    }

    @Test
    public void testGetByApplicationId() {
        ProjectResource projectResource = new ProjectResource();

        setupGetWithRestResultExpectations(projectRestURL + "/application/" + 123L, ProjectResource.class, projectResource);

        RestResult<ProjectResource> result = service.getByApplicationId(123L);

        assertTrue(result.isSuccess());

        assertEquals(projectResource, result.getSuccess());
    }

    @Test
    public void testGetProjectManager() {
        ProjectUserResource returnedResponse = new ProjectUserResource();
        setupGetWithRestResultExpectations(projectRestURL + "/123/project-manager", ProjectUserResource.class, returnedResponse);
        ProjectUserResource result = service.getProjectManager(123L).getSuccess();
        assertEquals(returnedResponse, result);
    }

    @Test
    public void testGetProjectManagerNotFound() {
        setupGetWithRestResultExpectations(projectRestURL + "/123/project-manager", ProjectUserResource.class, null, NOT_FOUND);
        Optional<ProjectUserResource> result = service.getProjectManager(123L).toOptionalIfNotFound().getSuccess();
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetPartnerOrganisation() {
        PartnerOrganisationResource partnerOrg = new PartnerOrganisationResource();
        setupGetWithRestResultExpectations(projectRestURL + "/123/partner/234", PartnerOrganisationResource.class, partnerOrg);
        RestResult<PartnerOrganisationResource> result = service.getPartnerOrganisation(123L, 234L);
        assertTrue(result.isSuccess());
        assertEquals(partnerOrg, result.getSuccess());
    }

    @Test
    public void testGetPartnerOrganisationNotFound() {
        setupGetWithRestResultExpectations(projectRestURL + "/123/partner/234", PartnerOrganisationResource.class, null, NOT_FOUND);
        RestResult<PartnerOrganisationResource> result = service.getPartnerOrganisation(123L, 234L);
        assertTrue(result.isFailure());
    }

    @Test
    public void testCreateProjectFromApplicationId() {
        ProjectResource projectResource = newProjectResource().build();
        setupPostWithRestResultExpectations(projectRestURL + "/create-project/application/123", ProjectResource.class, null,  projectResource, OK);
        RestResult<ProjectResource> result = service.createProjectFromApplicationId(123L);
        assertTrue(result.isSuccess());
        setupPostWithRestResultVerifications(projectRestURL + "/create-project/application/123", ProjectResource.class);
    }

    @Test
    public void testWithdrawProject() {
        long projectId = 123L;
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/withdraw", null, OK );
        RestResult<Void> result = service.withdrawProject(projectId);
        setupPostWithRestResultVerifications(projectRestURL + "/" + projectId + "/withdraw", Void.class);
        assertTrue(result.isSuccess());
    }
}
