package com.worth.ifs.application.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.resource.ApplicationResource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;


import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.applicationResourceListType;
import static com.worth.ifs.user.resource.UserRoleType.APPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * Tests to check the ApplicationRestService's interaction with the RestTemplate and the processing of its results
 */
public class ApplicationRestServiceMocksTest extends BaseRestServiceUnitTest<ApplicationRestServiceImpl> {

    private static final String applicationRestURL = "/application";

    @Override
    protected ApplicationRestServiceImpl registerRestServiceUnderTest() {
        ApplicationRestServiceImpl applicationRestService = new ApplicationRestServiceImpl();
        return applicationRestService;
    }


    @Test
    public void test_getApplicationById() {

        String expectedUrl = applicationRestURL + "/" + 123;
        ApplicationResource response = new ApplicationResource();//newApplicationResource().build();
        setupGetWithRestResultExpectations(expectedUrl, ApplicationResource.class, response);

        // now run the method under test
        ApplicationResource application = service.getApplicationById(123L).getSuccessObject();
        assertNotNull(application);
        Assert.assertEquals(response, application);
    }

    @Test
    public void test_getApplicationsByCompetitionIdAndUserId() {

        String expectedUrl = applicationRestURL + "/getApplicationsByCompetitionIdAndUserId/123/456/APPLICANT";
        List<ApplicationResource> returnedApplications = Arrays.asList(1,2,3).stream().map(i -> new ApplicationResource()).collect(Collectors.toList());// newApplicationResource().build(3);
        setupGetWithRestResultExpectations(expectedUrl, applicationResourceListType(), returnedApplications);

        // now run the method under test
        List<ApplicationResource> applications = service.getApplicationsByCompetitionIdAndUserId(123L, 456L, APPLICANT).getSuccessObject();
        assertNotNull(applications);
        assertEquals(returnedApplications, applications);
    }

    @Test
    public void test_getApplicationsByUserId() {

        String expectedUrl = applicationRestURL + "/findByUser/123";
        List<ApplicationResource> returnedApplications = Arrays.asList(1,2,3).stream().map(i -> new ApplicationResource()).collect(Collectors.toList());//newApplicationResource().build(3);
        setupGetWithRestResultExpectations(expectedUrl, applicationResourceListType(), returnedApplications);

        // now run the method under test
        List<ApplicationResource> applications = service.getApplicationsByUserId(123L).getSuccessObject();

        assertNotNull(applications);
        assertEquals(returnedApplications, applications);
    }

    @Test
    public void test_getCompleteQuestionsPercentage() throws Exception {

        String expectedUrl =  BaseRestServiceUnitTest.dataServicesUrl + applicationRestURL + "/getProgressPercentageByApplicationId/123";
        ObjectNode returnedDetails = new ObjectMapper().createObjectNode().put("completedPercentage", "60.5");

        when(mockAsyncRestTemplate.exchange(expectedUrl, HttpMethod.GET, httpEntityForRestCall(), ObjectNode.class)).thenReturn(settable(new ResponseEntity<>(returnedDetails, OK)));

        // now run the method under test
        Double percentage = service.getCompleteQuestionsPercentage(123L).get().getSuccessObject();

        assertNotNull(percentage);
        assertEquals(Double.valueOf(60.5), percentage);
    }

    @Test
    public void test_saveApplication() {

        String expectedUrl = applicationRestURL + "/saveApplicationDetails/123";
        ApplicationResource applicationToUpdate = new ApplicationResource(); // newApplicationResource().withId(123L).build();
        applicationToUpdate.setId(123L);
        ResponseEntity<String> response = new ResponseEntity<>("", OK);
        setupPostWithRestResultExpectations(expectedUrl, Void.class, applicationToUpdate, null, OK);

        // now run the method under test
        service.saveApplication(applicationToUpdate);
    }

    @Test
    public void test_updateApplicationStatus() {

        String expectedUrl = applicationRestURL + "/updateApplicationStatus?applicationId=123&statusId=456";
        setupPutWithRestResultExpectations(expectedUrl, Void.class, null, null);
        // now run the method under test
        service.updateApplicationStatus(123L, 456L);
    }

    @Test
    public void test_createApplication() {
        String expectedUrl = applicationRestURL + "/createApplicationByName/123/456";

        ApplicationResource application = new ApplicationResource();
        application.setName("testApplicationName123");

        setupPostWithRestResultExpectations(expectedUrl, ApplicationResource.class, application, application, CREATED);

        // now run the method under test
        ApplicationResource returnedResponse = service.createApplication(123L, 456L, "testApplicationName123").getSuccessObject();
        Assert.assertEquals(returnedResponse.getName(), application.getName());
    }
}
