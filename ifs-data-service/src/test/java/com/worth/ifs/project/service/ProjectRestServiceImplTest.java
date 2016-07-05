package com.worth.ifs.project.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.builder.MonitoringOfficerResourceBuilder;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.address.resource.OrganisationAddressType.REGISTERED;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.projectResourceListType;
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

    @Test
    public void testUpdateProjectAddress(){

        AddressResource addressResource = newAddressResource().build();

        setupPostWithRestResultExpectations(projectRestURL + "/123/address?addressType=" + REGISTERED.name() + "&leadOrganisationId=456", addressResource, OK);

        RestResult<Void> result = service.updateProjectAddress(456L, 123L, REGISTERED, addressResource);

        assertTrue(result.isSuccess());

    }

    @Test
    public void testFindByUserId(){

        List<ProjectResource> projects = newProjectResource().build(2);

        setupGetWithRestResultExpectations(projectRestURL + "/user/" + 1L, projectResourceListType(), projects);

        RestResult<List<ProjectResource>> result = service.findByUserId(1L);

        assertTrue(result.isSuccess());

        assertEquals(projects, result.getSuccessObject());

    }

    @Test
    public void testGetByApplicationId(){
        ProjectResource projectResource = newProjectResource().build();

        setupGetWithRestResultExpectations(projectRestURL + "/application/" + 123L, ProjectResource.class, projectResource);

        RestResult<ProjectResource> result = service.getByApplicationId(123L);

        assertTrue(result.isSuccess());

        assertEquals(projectResource, result.getSuccessObject());
    }

    @Test
    public void testSetApplicationDetailsSubmitted(){
        setupPostWithRestResultExpectations(projectRestURL + "/" + 123L + "/setApplicationDetailsSubmitted", null, OK);

        RestResult<Void> result = service.setApplicationDetailsSubmitted(123L);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testIsSubmitAllowed(){
        Boolean isAllowed = true;

        setupGetWithRestResultExpectations(projectRestURL + "/" + 123L + "/isSubmitAllowed", Boolean.class, isAllowed);

        RestResult<Boolean> result = service.isSubmitAllowed(123L);

        assertTrue(result.isSuccess());

        assertEquals(isAllowed, result.getSuccessObject());
    }

    @Test
    public void testUpdateMonitoringOfficer(){

        Long projectId = 1L;

        MonitoringOfficerResource monitoringOfficerResource = MonitoringOfficerResourceBuilder.newMonitoringOfficerResource()
                .withId(null)
                .withProject(projectId)
                .withFirstName("abc")
                .withLastName("xyz")
                .withEmail("abc.xyz@gmail.com")
                .withPhoneNumber("078323455")
                .build();

        setupPutWithRestResultExpectations(projectRestURL + "/" + projectId + "/monitoring-officer", monitoringOfficerResource, OK);

        RestResult<Void> result = service.updateMonitoringOfficer(projectId, "abc", "xyz", "abc.xyz@gmail.com", "078323455");

        assertTrue(result.isSuccess());

    }

    @Test
    public void testGetMonitoringOfficerForProject(){

        MonitoringOfficerResource expectedMonitoringOfficerResource = MonitoringOfficerResourceBuilder.newMonitoringOfficerResource()
                .withProject(1L)
                .withFirstName("abc")
                .withLastName("xyz")
                .withEmail("abc.xyz@gmail.com")
                .withPhoneNumber("078323455")
                .build();

        setupGetWithRestResultExpectations(projectRestURL + "/1/monitoring-officer", MonitoringOfficerResource.class, expectedMonitoringOfficerResource);

        RestResult<MonitoringOfficerResource> result = service.getMonitoringOfficerForProject(1L);

        assertTrue(result.isSuccess());

        assertEquals(expectedMonitoringOfficerResource, result.getSuccessObject());

    }
}
