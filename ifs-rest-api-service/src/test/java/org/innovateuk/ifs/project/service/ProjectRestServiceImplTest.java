package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.junit.Test;

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
    private static final String PROJECT_REST_URL = "/project";

    @Override
    protected ProjectRestServiceImpl registerRestServiceUnderTest() {
        ProjectRestServiceImpl projectService = new ProjectRestServiceImpl();
        return projectService;
    }

    @Test
    public void getProjectById() {
        ProjectResource returnedResponse = new ProjectResource();
        setupGetWithRestResultExpectations(PROJECT_REST_URL + "/123", ProjectResource.class, returnedResponse);
        ProjectResource result = service.getProjectById(123L).getSuccess();
        assertEquals(returnedResponse, result);
    }

    @Test
    public void getProjectUsers() {
        List<ProjectUserResource> users = Stream.of(1,2,3).map(i -> new ProjectUserResource()).collect(Collectors.toList());
        setupGetWithRestResultExpectations(PROJECT_REST_URL + "/123/project-users", projectUserResourceList(), users);
        RestResult<List<ProjectUserResource>> result = service.getProjectUsersForProject(123L);
        assertTrue(result.isSuccess());
        assertEquals(users, result.getSuccess());
    }

    @Test
    public void findByUserId() {
        List<ProjectResource> projects = Stream.of(1,2,3).map(i -> new ProjectResource()).collect(Collectors.toList());

        setupGetWithRestResultExpectations(PROJECT_REST_URL + "/user/" + 1L, projectResourceListType(), projects);

        RestResult<List<ProjectResource>> result = service.findByUserId(1L);

        assertTrue(result.isSuccess());

        assertEquals(projects, result.getSuccess());

    }

    @Test
    public void getByApplicationId() {
        ProjectResource projectResource = new ProjectResource();

        setupGetWithRestResultExpectations(PROJECT_REST_URL + "/application/" + 123L, ProjectResource.class, projectResource);

        RestResult<ProjectResource> result = service.getByApplicationId(123L);

        assertTrue(result.isSuccess());

        assertEquals(projectResource, result.getSuccess());
    }

    @Test
    public void getProjectManager() {
        ProjectUserResource returnedResponse = new ProjectUserResource();
        setupGetWithRestResultExpectations(PROJECT_REST_URL + "/123/project-manager", ProjectUserResource.class, returnedResponse);
        ProjectUserResource result = service.getProjectManager(123L).getSuccess();
        assertEquals(returnedResponse, result);
    }

    @Test
    public void getProjectManagerNotFound() {
        setupGetWithRestResultExpectations(PROJECT_REST_URL + "/123/project-manager", ProjectUserResource.class, null, NOT_FOUND);
        Optional<ProjectUserResource> result = service.getProjectManager(123L).toOptionalIfNotFound().getSuccess();
        assertFalse(result.isPresent());
    }

    @Test
    public void getPartnerOrganisation() {
        PartnerOrganisationResource partnerOrg = new PartnerOrganisationResource();
        setupGetWithRestResultExpectations(PROJECT_REST_URL + "/123/partner/234", PartnerOrganisationResource.class, partnerOrg);
        RestResult<PartnerOrganisationResource> result = service.getPartnerOrganisation(123L, 234L);
        assertTrue(result.isSuccess());
        assertEquals(partnerOrg, result.getSuccess());
    }

    @Test
    public void getPartnerOrganisationNotFound() {
        setupGetWithRestResultExpectations(PROJECT_REST_URL + "/123/partner/234", PartnerOrganisationResource.class, null, NOT_FOUND);
        RestResult<PartnerOrganisationResource> result = service.getPartnerOrganisation(123L, 234L);
        assertTrue(result.isFailure());
    }

    @Test
    public void createProjectFromApplicationId() {
        ProjectResource projectResource = newProjectResource().build();
        setupPostWithRestResultExpectations(PROJECT_REST_URL + "/create-project/application/123", ProjectResource.class, null,  projectResource, OK);
        RestResult<ProjectResource> result = service.createProjectFromApplicationId(123L);
        assertTrue(result.isSuccess());
        setupPostWithRestResultVerifications(PROJECT_REST_URL + "/create-project/application/123", ProjectResource.class);
    }
}
