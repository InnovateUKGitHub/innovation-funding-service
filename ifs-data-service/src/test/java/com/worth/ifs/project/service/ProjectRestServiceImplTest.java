package com.worth.ifs.project.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.projectUserResourceList;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

        ProjectResource returnedResponse = newProjectResource().build();

        setupGetWithRestResultExpectations(projectRestURL + "/123", ProjectResource.class, returnedResponse);

        ProjectResource result = service.getProjectById(123L).getSuccessObject();
       
        assertEquals(returnedResponse, result);
    }
    
    @Test
    public void testUpdateFinanceContact() {

        setupPostWithRestResultExpectations(projectRestURL + "/123/organisation/5/finance-contact?financeContact=6", null, OK);

        RestResult<Void> result = service.updateFinanceContact(123L, 5L,  6L);
       
        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetProjectUsers() {

        List<ProjectUserResource> users = newProjectUserResource().build(3);

        setupGetWithRestResultExpectations(projectRestURL + "/123/project-users", projectUserResourceList(), users);

        RestResult<List<ProjectUserResource>> result = service.getProjectUsersForProject(123L);

        assertTrue(result.isSuccess());
        assertEquals(users, result.getSuccessObject());
    }

}
