package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.questionStatusResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.validationMessagesListType;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

public class QuestionStatusRestServiceMocksTest extends BaseRestServiceUnitTest<QuestionStatusRestServiceImpl> {
    private static final String questionStatusRestURL = "/questionStatus";

    @Override
    protected QuestionStatusRestServiceImpl registerRestServiceUnderTest() {
        QuestionStatusRestServiceImpl questionStatusRestService = new QuestionStatusRestServiceImpl();
        return questionStatusRestService;
    }

    @Test
    public void assignTest() {

        setupPutWithRestResultExpectations(questionStatusRestURL + "/assign/1/2/3/4", Void.class, null, null);

        // now run the method under test
        assertTrue(service.assign(1L, 2L, 3L, 4L).isSuccess());
    }

    @Test
    public void getMarkedAsCompleteTest() throws Exception {
        String expectedUrl = BaseRestServiceUnitTest.dataServicesUrl + questionStatusRestURL + "/get-marked-as-complete/1/2";

        Long[] questionIds = new Long[]{3L, 4L, 5L};
        when(mockAsyncRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(""), Long[].class)).thenReturn(settable(new ResponseEntity<>(questionIds, HttpStatus.OK)));

        // now run the method under test
        Set<Long> returnedQuestionIds = service.getMarkedAsComplete(1L, 2L).get();

        // verify
        assertNotNull(questionIds);
        assertEquals(3, returnedQuestionIds.size());
        assertEquals(new HashSet<>(Arrays.asList(questionIds)), returnedQuestionIds);
    }

    @Test
    public void markAsCompleteTest() {
        setupPutWithRestResultExpectations(questionStatusRestURL + "/mark-as-complete/1/2/3", validationMessagesListType(), null, null, HttpStatus.OK);
        assertTrue(service.markAsComplete(1L, 2L, 3L).isSuccess());
    }

    @Test
    public void markAsInCompleteTest() {
        setupPutWithRestResultExpectations(questionStatusRestURL + "/mark-as-in-complete/1/2/3", Void.class, null, null);
        assertTrue(service.markAsInComplete(1L, 2L, 3L).isSuccess());
    }

    @Test
    public void updateNotificationTest() {
        setupPutWithRestResultExpectations(questionStatusRestURL + "/update-notification/1/true", Void.class, null, null);
        assertTrue(service.updateNotification(1L, true).isSuccess());
    }


    @Test
    public void findQuestionStatusesByQuestionAndApplicationIdTest() {

        List<QuestionStatusResource> questionStatuses = Arrays.asList(1,2,3).stream().map(i -> new QuestionStatusResource()).collect(Collectors.toList());// newQuestionStatusResource().build(3);
        setupGetWithRestResultExpectations(questionStatusRestURL + "/findByQuestionAndApplication/1/2", questionStatusResourceListType(), questionStatuses);

        List<QuestionStatusResource> returnedQuestionStatuses = service.findQuestionStatusesByQuestionAndApplicationId(1L, 2L).getSuccess();
        assertEquals(questionStatuses, returnedQuestionStatuses);
    }


    @Test
    public void findByQuestionAndApplicationAndOrganisationTest() {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long organisationId = 3L;

        List<QuestionStatusResource> questionStatuses = Arrays.asList(1,2,3).stream().map(i -> new QuestionStatusResource()).collect(Collectors.toList());
        setupGetWithRestResultExpectations(questionStatusRestURL + "/findByQuestionAndApplicationAndOrganisation/" + questionId + "/" + applicationId + "/" + organisationId, questionStatusResourceListType(), questionStatuses);

        List<QuestionStatusResource> returnedQuestionStatuses = service.findByQuestionAndApplicationAndOrganisation(questionId, applicationId, organisationId).getSuccess();
        assertEquals(questionStatuses, returnedQuestionStatuses);
    }

    @Test
    public void findByApplicationAndOrganisationTest() {
        Long applicationId = 2L;
        Long organisationId = 3L;

        List<QuestionStatusResource> questionStatuses = Arrays.asList(1,2,3).stream().map(i -> new QuestionStatusResource()).collect(Collectors.toList());
        setupGetWithRestResultExpectations(questionStatusRestURL + "/findByApplicationAndOrganisation/" + applicationId + "/" + organisationId, questionStatusResourceListType(), questionStatuses);

        List<QuestionStatusResource> returnedQuestionStatuses = service.findByApplicationAndOrganisation(applicationId, organisationId).getSuccess();
        assertEquals(questionStatuses, returnedQuestionStatuses);
    }

    @Test
    public void findQuestionStatusByIdTest() {
        Long id = 2L;

        QuestionStatusResource questionStatus = new QuestionStatusResource();
        setupGetWithRestResultExpectations(questionStatusRestURL + "/" + id, QuestionStatusResource.class, questionStatus);

        QuestionStatusResource returnedQuestionStatus = service.findQuestionStatusById(id).getSuccess();
        Assert.assertEquals(questionStatus, returnedQuestionStatus);
    }

    @Test
    public void getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationIdTest() {
        List<Long> questionIds = asList(1L, 2L);
        Long applicationId = 2L;
        Long organisationId = 3L;

        List<QuestionStatusResource> questionStatuses = Arrays.asList(1,2,3).stream().map(i -> new QuestionStatusResource()).collect(Collectors.toList());
        setupGetWithRestResultExpectations(questionStatusRestURL + "/findByQuestionIdsAndApplicationIdAndOrganisationId/" + simpleJoiner(questionIds, ",") + "/" + applicationId + "/" + organisationId, questionStatusResourceListType(), questionStatuses);

        List<QuestionStatusResource> returnedQuestionStatuses = service.getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(questionIds, applicationId, organisationId).getSuccess();
        assertEquals(questionStatuses, returnedQuestionStatuses);
    }

    @Test
    public void markTeamAsInComplete() {
        long questionId = 1L;
        long applicationId = 2L;
        long markedAsInCompleteById = 3L;

        setupPutWithRestResultExpectations(format("%s/mark-team-as-in-complete/%s/%s/%s", questionStatusRestURL,
                questionId, applicationId, markedAsInCompleteById), Void.class, null, null);
        assertTrue(service.markTeamAsInComplete(questionId, applicationId, markedAsInCompleteById).isSuccess());
    }

}
