package com.worth.ifs.project.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void testUpdateFincanceContact() {

        setupPostWithRestResultExpectations(projectRestURL + "/123/finance-contact/5?financeContact=6", null, HttpStatus.OK);

        RestResult<Void> result = service.updateFinanceContact(123L, 5L,  6L);
       
        assertTrue(result.isSuccess());
    }

}
