package com.worth.ifs.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.resource.ApplicationResource;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.user.domain.UserRoleType.APPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

/**
 * Tests to check the ApplicationRestService's interaction with the RestTemplate and the processing of its results
 */
public class ApplicationRestServiceMocksTest extends BaseRestServiceUnitTest<ApplicationRestServiceImpl> {

    private static final String applicationRestURL = "/applications";

    @Override
    protected ApplicationRestServiceImpl registerRestServiceUnderTest() {
        ApplicationRestServiceImpl applicationRestService = new ApplicationRestServiceImpl();
        applicationRestService.applicationRestURL = applicationRestURL;
        return applicationRestService;
    }


    @Test
    public void test_getApplicationById() {

        String expectedUrl = dataServicesUrl + applicationRestURL + "/normal/" + 123;
        ResponseEntity<ApplicationResource> response = new ResponseEntity<>(newApplicationResource().build(), OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), ApplicationResource.class)).thenReturn(response);

        // now run the method under test
        ApplicationResource application = service.getApplicationById(123L);
        assertNotNull(application);
        assertEquals(application,response.getBody());
    }

    @Test
    public void test_getApplicationsByCompetitionIdAndUserId() {

        String expectedUrl = dataServicesUrl + applicationRestURL + "/getApplicationsByCompetitionIdAndUserId/123/456/APPLICANT";
        ApplicationResource[] returnedApplications = newApplicationResource().buildArray(3, ApplicationResource.class);
        ResponseEntity<ApplicationResource[]> response = new ResponseEntity<>(returnedApplications, OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), ApplicationResource[].class)).thenReturn(response);

        // now run the method under test
        List<ApplicationResource> applications = service.getApplicationsByCompetitionIdAndUserId(123L, 456L, APPLICANT);
        assertNotNull(applications);
        assertEquals(3, applications.size());
        assertEquals(returnedApplications[0], applications.get(0));
        assertEquals(returnedApplications[1], applications.get(1));
        assertEquals(returnedApplications[2], applications.get(2));
    }

    @Test
    public void test_getApplicationsByUserId() {

        String expectedUrl = dataServicesUrl + applicationRestURL + "/findByUser/123";
        ApplicationResource[] returnedApplications = newApplicationResource().buildArray(3, ApplicationResource.class);
        ResponseEntity<ApplicationResource[]> response = new ResponseEntity<>(returnedApplications, OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), ApplicationResource[].class)).thenReturn(response);

        // now run the method under test
        List<ApplicationResource> applications = service.getApplicationsByUserId(123L);

        assertNotNull(applications);
        assertEquals(3, applications.size());
        assertEquals(returnedApplications[0], applications.get(0));
        assertEquals(returnedApplications[1], applications.get(1));
        assertEquals(returnedApplications[2], applications.get(2));
    }

    @Test
    public void test_getCompleteQuestionsPercentage() {

        String expectedUrl = dataServicesUrl + applicationRestURL + "/getProgressPercentageByApplicationId/123";
        ObjectNode returnedDetails = new ObjectMapper().createObjectNode().put("completedPercentage", "60.5");

        ResponseEntity<ObjectNode> response = new ResponseEntity<>(returnedDetails, OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), ObjectNode.class)).thenReturn(response);

        // now run the method under test
        Double percentage = service.getCompleteQuestionsPercentage(123L);

        assertNotNull(percentage);
        assertEquals(Double.valueOf(60.5), percentage);
    }

    @Test
    public void test_saveApplication() {

        String expectedUrl = dataServicesUrl + applicationRestURL + "/saveApplicationDetails/123";
        ApplicationResource applicationToUpdate = newApplicationResource().withId(123L).build();

        ResponseEntity<String> response = new ResponseEntity<>("", OK);
        when(mockRestTemplate.postForEntity(expectedUrl, httpEntityForRestCall(applicationToUpdate), String.class)).thenReturn(response);

        // now run the method under test
        service.saveApplication(applicationToUpdate);
    }

    @Test
    public void test_updateApplicationStatus() {

        String expectedUrl = dataServicesUrl + applicationRestURL + "/updateApplicationStatus?applicationId=123&statusId=456";

        ResponseEntity<String> response = new ResponseEntity<>("", OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), String.class)).thenReturn(response);

        // now run the method under test
        service.updateApplicationStatus(123L, 456L);
    }

    @Test
    public void test_createApplication() {
        String expectedUrl = dataServicesUrl + applicationRestURL + "/createApplicationByName/123/456";

        ApplicationResource application = new ApplicationResource();
        application.setName("testApplicationName123");

        ResponseEntity<ApplicationResource> response = new ResponseEntity<>(application, OK);

        when(mockRestTemplate.postForEntity(eq(expectedUrl), isA(HttpEntity.class), eq(ApplicationResource.class))).thenReturn(response);

        // now run the method under test
        ApplicationResource returnedResponse = service.createApplication(123L, 456L, "testApplicationName123");
        assertEquals(returnedResponse.getName(),application.getName());
    }
}
