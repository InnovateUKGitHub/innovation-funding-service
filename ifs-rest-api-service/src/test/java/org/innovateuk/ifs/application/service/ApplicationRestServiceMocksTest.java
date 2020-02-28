package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder.newApplicationIneligibleSendResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.applicationResourceListType;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * Tests to check the ApplicationRestService's interaction with the RestTemplate and the processing of its results
 */
public class ApplicationRestServiceMocksTest extends BaseRestServiceUnitTest<ApplicationRestServiceImpl> {

    private static final String applicationRestURL = "/application";
    private String processRoleRestURL = "/processrole";
    private String questionStatusRestURL = "/question-status";

    @Override
    protected ApplicationRestServiceImpl registerRestServiceUnderTest() {
        ApplicationRestServiceImpl applicationRestService = new ApplicationRestServiceImpl();
        return applicationRestService;
    }


    @Test
    public void getApplicationById() {

        String expectedUrl = applicationRestURL + "/" + 123;
        ApplicationResource response = new ApplicationResource();//newApplicationResource().build();
        setupGetWithRestResultExpectations(expectedUrl, ApplicationResource.class, response);

        // now run the method under test
        ApplicationResource application = service.getApplicationById(123L).getSuccess();
        assertNotNull(application);
        Assert.assertEquals(response, application);
    }

    @Test
    public void getApplicationsByCompetitionIdAndUserId() {

        String expectedUrl = applicationRestURL + "/get-applications-by-competition-id-and-user-id/123/456/APPLICANT";
        List<ApplicationResource> returnedApplications = Stream.of(1, 2, 3).map(i -> new ApplicationResource()).collect(Collectors.toList());// newApplicationResource().build(3);
        setupGetWithRestResultExpectations(expectedUrl, applicationResourceListType(), returnedApplications);

        // now run the method under test
        List<ApplicationResource> applications = service.getApplicationsByCompetitionIdAndUserId(123L, 456L, APPLICANT).getSuccess();
        assertNotNull(applications);
        assertEquals(returnedApplications, applications);
    }

    @Test
    public void getApplicationsByUserId() {

        String expectedUrl = applicationRestURL + "/find-by-user/123";
        List<ApplicationResource> returnedApplications = Stream.of(1, 2, 3).map(i -> new ApplicationResource()).collect(Collectors.toList());//newApplicationResource().build(3);
        setupGetWithRestResultExpectations(expectedUrl, applicationResourceListType(), returnedApplications);

        // now run the method under test
        List<ApplicationResource> applications = service.getApplicationsByUserId(123L).getSuccess();

        assertNotNull(applications);
        assertEquals(returnedApplications, applications);
    }

    @Test
    public void wildcardSearchById() {

        String searchString = "12";
        int pageNumber = 0;
        int pageSize = 5;

        String expectedUrl = applicationRestURL + "/wildcard-search-by-id?searchString=12&page=0&size=5";
        ApplicationPageResource applicationPageResource = new ApplicationPageResource();
        setupGetWithRestResultExpectations(expectedUrl, ApplicationPageResource.class, applicationPageResource);

        RestResult<ApplicationPageResource> result = service.wildcardSearchById(searchString, pageNumber, pageSize);
        assertTrue(result.isSuccess());
        assertEquals(applicationPageResource, result.getSuccess());

        setupGetWithRestResultVerifications(expectedUrl, null, ApplicationPageResource.class);
    }

    @Test
    public void getCompleteQuestionsPercentage() throws Exception {

        Double returnedResponse = 60.5;

        String expectedUrl = applicationRestURL + "/get-progress-percentage-by-application-id/123";
        setupGetWithRestResultAsyncExpectations(expectedUrl, Double.class, returnedResponse);

        Double percentage = service.getCompleteQuestionsPercentage(123L).get().getSuccess();
        assertEquals(returnedResponse, percentage);
    }

    @Test
    public void saveApplication() {

        String expectedUrl = applicationRestURL + "/save-application-details/123";
        ApplicationResource applicationToUpdate = new ApplicationResource(); // newApplicationResource().withId(123L).build();
        applicationToUpdate.setId(123L);
        ResponseEntity<String> response = new ResponseEntity<>("", OK);
        setupPostWithRestResultExpectations(expectedUrl, ValidationMessages.class, applicationToUpdate, null, OK);

        // now run the method under test
        service.saveApplication(applicationToUpdate);
    }

    @Test
    public void updateApplicationStatus() {

        String expectedUrl = applicationRestURL + "/update-application-state?applicationId=123&state=APPROVED";
        setupPutWithRestResultExpectations(expectedUrl, Void.class, null, null);
        // now run the method under test
        service.updateApplicationState(123L, ApplicationState.APPROVED);
    }

    @Test
    public void createApplication() {
        String expectedUrl = applicationRestURL + "/create-application-by-name/123/456/789";

        ApplicationResource application = new ApplicationResource();
        application.setName("testApplicationName123");

        setupPostWithRestResultExpectations(expectedUrl, ApplicationResource.class, application, application, CREATED);

        // now run the method under test
        ApplicationResource returnedResponse = service.createApplication(123L, 456L, 789L, "testApplicationName123").getSuccess();
        Assert.assertEquals(returnedResponse.getName(), application.getName());
    }

    @Test
    public void findByProcessRoleId() {
        long processRoleId = 1L;
        String expectedUrl = processRoleRestURL + "/" + processRoleId + "/application";
        ApplicationResource application = newApplicationResource().build();

        setupGetWithRestResultExpectations(expectedUrl, ApplicationResource.class, application);

        // now run the method under test
        ApplicationResource returnedResponse = service.findByProcessRoleId(processRoleId).getSuccess();
        assertEquals(returnedResponse, application);
    }

    @Test
    public void getAssignedQuestionsCount() {
        long applicationId = 1L;
        long assigneeId = 1L;
        String expectedUrl = questionStatusRestURL + "/get-assigned-questions-count-by-application-id-and-assignee-id/" + applicationId + "/" + assigneeId;
        int count = 1;

        setupGetWithRestResultExpectations(expectedUrl, Integer.class, count);

        // now run the method under test
        Integer actualCount = service.getAssignedQuestionsCount(applicationId, assigneeId).getSuccess();
        assertEquals(actualCount, Integer.valueOf(count));
    }

    @Test
    public void markAsIneligible() {
        long applicationId = 123L;
        IneligibleOutcomeResource reason = new IneligibleOutcomeResource("reason");
        String expectedUrl = applicationRestURL + "/" + applicationId + "/ineligible";

        setupPostWithRestResultExpectations(expectedUrl, reason, HttpStatus.OK);

        service.markAsIneligible(applicationId, reason);
    }

    @Test
    public void informIneligible() {
        long applicationId = 1L;
        ApplicationIneligibleSendResource applicationIneligibleSendResource = newApplicationIneligibleSendResource().build();

        setupPostWithRestResultExpectations(applicationRestURL + "/inform-ineligible/" + applicationId, Void.class, applicationIneligibleSendResource, null, OK);
        service.informIneligible(applicationId, applicationIneligibleSendResource).getSuccess();
    }

    @Test
    public void getLatestEmailFundingDate() {
        long competitionId = 1L;
        ZonedDateTime returnedDate = ZonedDateTime.now();
        String expectedUrl = applicationRestURL + "/get-latest-email-funding-date/" + competitionId;

        setupGetWithRestResultExpectations(expectedUrl, ZonedDateTime.class, returnedDate);

        ZonedDateTime date = service.getLatestEmailFundingDate(competitionId).getSuccess();
        assertEquals(returnedDate, date);
    }

    @Test
    public void hideApplication() {
        long applicationId = 1L;
        long userId = 1L;
        String expectedUrl = format("%s/%d/hide-for-user/%d", applicationRestURL, applicationId, userId);

        setupPostWithRestResultVerifications(expectedUrl, Void.class);

        RestResult<Void> result = service.hideApplication(applicationId, userId);
        assertTrue(result.isSuccess());
    }

    @Test
    public void deleteApplication() {
        long applicationId = 1L;
        String expectedUrl = format("%s/%d", applicationRestURL, applicationId);

        setupDeleteWithRestResultVerifications(expectedUrl);

        RestResult<Void> result = service.deleteApplication(applicationId);
        assertTrue(result.isSuccess());
    }
}
