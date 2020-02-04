package org.innovateuk.ifs.project.service;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectUserResourceList;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;


import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.junit.Test;

public class ProjectRestServiceImplTest extends BaseRestServiceUnitTest<ProjectRestServiceImpl> {
    private static final String PROJECT_REST_URL = "/project";

    @Override
    protected ProjectRestServiceImpl registerRestServiceUnderTest() {
        return new ProjectRestServiceImpl();
    }

    @Test
    public void getProjectById() {
        ProjectResource returnedResponse = newProjectResource().build();

        setupGetWithRestResultExpectations(format(PROJECT_REST_URL + "/%d", returnedResponse.getId()), ProjectResource.class, returnedResponse);

        ProjectResource result = service.getProjectById(returnedResponse.getId()).getSuccess();

        assertEquals(returnedResponse, result);
    }

    @Test
    public void getProjectUsers() {
        long projectId = 11L;
        List<ProjectUserResource> users = newProjectUserResource().build(3);

        setupGetWithRestResultExpectations(format(PROJECT_REST_URL + "/%d/project-users", projectId), projectUserResourceList(), users);

        RestResult<List<ProjectUserResource>> result = service.getProjectUsersForProject(projectId);

        assertEquals(users, result.getSuccess());
    }

    @Test
    public void findByUserId() {
        long userId = 7L;
        List<ProjectResource> projects = Stream.of(1,2,3).map(i -> new ProjectResource()).collect(toList());

        setupGetWithRestResultExpectations(format(PROJECT_REST_URL + "/user/%d", userId), projectResourceListType(), projects);

        List<ProjectResource> result = service.findByUserId(userId).getSuccess();

        assertEquals(projects, result);
    }

    @Test
    public void getByApplicationId() {
        ProjectResource projectResource = newProjectResource().build();

        setupGetWithRestResultExpectations(format(PROJECT_REST_URL + "/application/%d", projectResource.getId()), ProjectResource.class, projectResource);

        ProjectResource result = service.getByApplicationId(projectResource.getId()).getSuccess();

        assertEquals(projectResource, result);
    }

    @Test
    public void getProjectManager() {
        long projectId = 23;
        ProjectUserResource returnedResponse = newProjectUserResource().withProject(projectId).build();
        setupGetWithRestResultExpectations(format(PROJECT_REST_URL + "/%d/project-manager", projectId), ProjectUserResource.class, returnedResponse);

        ProjectUserResource result = service.getProjectManager(projectId).getSuccess();

        assertEquals(returnedResponse, result);
    }

    @Test
    public void getProjectManager_notFound() {
        long projectId = 13;

        setupGetWithRestResultExpectations(format(PROJECT_REST_URL + "/%d/project-manager", projectId), ProjectUserResource.class, null, NOT_FOUND);

        Optional<ProjectUserResource> result = service.getProjectManager(projectId).toOptionalIfNotFound().getSuccess();

        assertFalse(result.isPresent());
    }

    @Test
    public void createProjectFromApplicationId() {
        ProjectResource projectResource = newProjectResource().build();
        setupPostWithRestResultExpectations(format(PROJECT_REST_URL + "/create-project/application/%d", projectResource.getId()), ProjectResource.class, null,  projectResource, OK);

        RestResult<ProjectResource> result = service.createProjectFromApplicationId(projectResource.getId());

        assertTrue(result.isSuccess());

        setupPostWithRestResultVerifications(format(PROJECT_REST_URL + "/create-project/application/%d", projectResource.getId()), ProjectResource.class);
    }

    @Test
    public void getLeadOrganisationByProject() {
        long projectId = 17;
        OrganisationResource organisation = newOrganisationResource().build();

        setupGetWithRestResultExpectations(format(PROJECT_REST_URL + "/%d/lead-organisation", projectId), OrganisationResource.class, organisation);

        OrganisationResource result = service.getLeadOrganisationByProject(projectId).getSuccess();

        assertEquals(organisation, result);
    }
}