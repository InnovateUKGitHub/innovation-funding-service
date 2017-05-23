package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectUserResourceList;

import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.*;

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
        ProjectResource result = service.getProjectById(123L).getSuccessObject();
        Assert.assertEquals(returnedResponse, result);
    }

    @Test
    public void testGetStatusByProjectId() {
        ProjectStatusResource returnedResponse = new ProjectStatusResource();
        setupGetWithRestResultExpectations(projectRestURL + "/123/status", ProjectStatusResource.class, returnedResponse);
        ProjectStatusResource result = service.getProjectStatus(123L).getSuccessObject();
        Assert.assertEquals(returnedResponse, result);
    }

    @Test
    public void testGetProjectUsers() {
        List<ProjectUserResource> users = Arrays.asList(1,2,3).stream().map(i -> new ProjectUserResource()).collect(Collectors.toList());
        setupGetWithRestResultExpectations(projectRestURL + "/123/project-users", projectUserResourceList(), users);
        RestResult<List<ProjectUserResource>> result = service.getProjectUsersForProject(123L);
        assertTrue(result.isSuccess());
        Assert.assertEquals(users, result.getSuccessObject());
    }

    @Test
    public void testFindByUserId() {

        List<ProjectResource> projects = Arrays.asList(1,2,3).stream().map(i -> new ProjectResource()).collect(Collectors.toList());

        setupGetWithRestResultExpectations(projectRestURL + "/user/" + 1L, projectResourceListType(), projects);

        RestResult<List<ProjectResource>> result = service.findByUserId(1L);

        assertTrue(result.isSuccess());

        Assert.assertEquals(projects, result.getSuccessObject());

    }

    @Test
    public void testGetByApplicationId() {
        ProjectResource projectResource = new ProjectResource();

        setupGetWithRestResultExpectations(projectRestURL + "/application/" + 123L, ProjectResource.class, projectResource);

        RestResult<ProjectResource> result = service.getByApplicationId(123L);

        assertTrue(result.isSuccess());

        Assert.assertEquals(projectResource, result.getSuccessObject());
    }

    @Test
    public void testGetProjectTeamStatus() {
        String expectedUrl = projectRestURL + "/123/team-status";

        setupGetWithRestResultExpectations(expectedUrl, ProjectTeamStatusResource.class, null, OK);

        RestResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(123L, Optional.empty());

        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetProjectTeamStatusWithFilterByUserId() {
        String expectedUrl = projectRestURL + "/123/team-status?filterByUserId=456";

        setupGetWithRestResultExpectations(expectedUrl, ProjectTeamStatusResource.class, null, OK);

        RestResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(123L, Optional.of(456L));

        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetProjectManager() {
        ProjectUserResource returnedResponse = new ProjectUserResource();
        setupGetWithRestResultExpectations(projectRestURL + "/123/project-manager", ProjectUserResource.class, returnedResponse);
        ProjectUserResource result = service.getProjectManager(123L).getSuccessObject();
        Assert.assertEquals(returnedResponse, result);
    }

    @Test
    public void testGetProjectManagerNotFound() {
        setupGetWithRestResultExpectations(projectRestURL + "/123/project-manager", ProjectUserResource.class, null, NOT_FOUND);
        Optional<ProjectUserResource> result = service.getProjectManager(123L).toOptionalIfNotFound().getSuccessObject();
        Assert.assertFalse(result.isPresent());
    }
}
