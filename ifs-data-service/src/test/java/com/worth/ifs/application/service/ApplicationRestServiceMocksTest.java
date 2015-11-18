package com.worth.ifs.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseRestServiceMocksTest;
import com.worth.ifs.application.domain.Application;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Consumer;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.user.domain.UserRoleType.APPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

/**
 * Tests to check the ApplicationRestService's interaction with the RestTemplate and the processing of its results
 */
public class ApplicationRestServiceMocksTest extends BaseRestServiceMocksTest<ApplicationRestServiceImpl> {

    private static final String applicationRestURL = "/applications";

    @Override
    protected ApplicationRestServiceImpl registerRestServiceUnderTest(Consumer<ApplicationRestServiceImpl> registrar) {
        ApplicationRestServiceImpl applicationRestService = new ApplicationRestServiceImpl();
        applicationRestService.applicationRestURL = applicationRestURL;
        registrar.accept(applicationRestService);
        return applicationRestService;
    }

    @Test
    public void test_getApplicationById() {

        String expectedUrl = dataServicesUrl + applicationRestURL + "/id/" + 123;
        ResponseEntity<Application> response = new ResponseEntity<>(newApplication().build(), OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestGet(), Application.class)).thenReturn(response);

        // now run the method under test
        Application application = service.getApplicationById(123L);
        assertNotNull(application);
        assertTrue(application == response.getBody());
    }

    @Test
    public void test_getApplicationsByCompetitionIdAndUserId() {

        String expectedUrl = dataServicesUrl + applicationRestURL + "/getApplicationsByCompetitionIdAndUserId/123/456/APPLICANT";
        Application[] returnedApplications = newApplication().buildArray(3, Application.class);
        ResponseEntity<Application[]> response = new ResponseEntity<>(returnedApplications, OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestGet(), Application[].class)).thenReturn(response);

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
        ResponseEntity<Application[]> response = new ResponseEntity<>(returnedApplications, OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestGet(), Application[].class)).thenReturn(response);

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

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode returnedDetails = mapper.createObjectNode().put("completedPercentage", "60.5");

        ResponseEntity<ObjectNode> response = new ResponseEntity<>(returnedDetails, OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestGet(), ObjectNode.class)).thenReturn(response);

        // now run the method under test
        Double percentage = service.getCompleteQuestionsPercentage(123L);

        assertNotNull(percentage);
        assertEquals(Double.valueOf(60.5), percentage);
    }
}
