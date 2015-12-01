package com.worth.ifs.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.user.domain.UserRoleType.APPLICANT;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.*;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
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

/*
    @Test
    public void test_getApplicationById() {
        ParameterizedTypeReference<ApplicationResource> responseType = new ParameterizedTypeReference<ApplicationResource>() {};
        String expectedUrl = dataServicesUrl + applicationRestURL + "/" + 1;
        ResponseEntity<ApplicationResource> response = new ResponseEntity<ApplicationResource>(new ApplicationResource(), OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, null, responseType)).thenReturn(response);

        // now run the method under test
        Application application = service.getApplicationById(1L);
        assertNotNull(application);
        assertTrue(application.equals(response.getBody()));

    }
*/
    @Test
    public void test_getApplicationsByCompetitionIdAndUserId() {

        String expectedUrl = dataServicesUrl + applicationRestURL + "/getApplicationsByCompetitionIdAndUserId/123/456/APPLICANT";
        Application[] returnedApplications = newApplication().buildArray(3, Application.class);
        ResponseEntity<Application[]> response = new ResponseEntity<>(returnedApplications, OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Application[].class)).thenReturn(response);

        // now run the method under test
        List<Application> applications = service.getApplicationsByCompetitionIdAndUserId(123L, 456L, APPLICANT);
        assertNotNull(applications);
        assertEquals(3, applications.size());
        assertEquals(returnedApplications[0], applications.get(0));
        assertEquals(returnedApplications[1], applications.get(1));
        assertEquals(returnedApplications[2], applications.get(2));
    }

    @Test
    public void test_getApplicationsByUserId() {
        String expectedUrl = dataServicesUrl + applicationRestURL + "/findByUser/123";
        Application[] returnedApplications = newApplication().buildArray(3, Application.class);
        Resources<ApplicationResource> applicationResources = mock(Resources.class);
        List<ApplicationResource> resources = new ArrayList<>();
        for (Application application: returnedApplications){
            ApplicationResource resource = mock(ApplicationResource.class);
            when(resource.toApplication()).thenReturn(application);
            resources.add(resource);
        }
        when (applicationResources.getContent()).thenReturn(resources);

        ParameterizedTypeReference<Resources<ApplicationResource>> parameterizedTypeRef = new ParameterizedTypeReference<Resources<ApplicationResource>>() {};
        when(mockRestTemplate.exchange(URI.create(expectedUrl), GET, httpEntityForRestCall(), parameterizedTypeRef)).thenReturn(new ResponseEntity<>(applicationResources, OK));

        // now run the method under test
        List<Application> applications = service.getApplicationsByUserId(123L);

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
        Application applicationToUpdate = newApplication().withId(123L).build();

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

        Application application = new Application();
        application.setName("testApplicationName123");

        ResponseEntity<Application> response = new ResponseEntity<>(application, OK);

        when(mockRestTemplate.exchange(eq(expectedUrl), eq(PUT), isA(HttpEntity.class), eq(Application.class))).thenReturn(response);

        // now run the method under test
        Application returnedResponse = service.createApplication(123L, 456L, "testApplicationName123");
        returnedResponse.getName().equals(application.getName());
    }
}
