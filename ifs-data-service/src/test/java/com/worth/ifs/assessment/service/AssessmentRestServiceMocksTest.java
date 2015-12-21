package com.worth.ifs.assessment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;

import static com.worth.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
public class AssessmentRestServiceMocksTest extends BaseRestServiceUnitTest<AssessmentRestServiceImpl> {

    private static String assessmentRestURL = "/assessments";

    @Override
    protected AssessmentRestServiceImpl registerRestServiceUnderTest() {
        AssessmentRestServiceImpl assessmentRestService = new AssessmentRestServiceImpl();
        assessmentRestService.assessmentRestURL = assessmentRestURL;
        return assessmentRestService;
    }

    @Test
    public void test_getAllByAssessorAndCompetition() {

        long assessorId = 123L;
        long competitionId = 456L;

        List<Assessment> assessments = newAssessment().build(3);
        ResponseEntity<Assessment[]> mockResponse = new ResponseEntity<>(assessments.toArray(new Assessment[assessments.size()]), OK);
        when(mockRestTemplate.exchange(eq(dataServicesUrl + assessmentRestURL + "/findAssessmentsByCompetition/123/456"), eq(GET), any(HttpEntity.class), eq(Assessment[].class))).thenReturn(mockResponse);

        List<Assessment> results = service.getAllByAssessorAndCompetition(assessorId, competitionId);
        assertEquals(3, results.size());
        forEachWithIndex(results, (i, result) -> assertEquals(assessments.get(i), result));
    }

    @Test
    public void test_acceptAssessmentInvitation() {

        String expectedUrl = dataServicesUrl + assessmentRestURL + "/acceptAssessmentInvitation/123";
        Assessment assessment = newAssessment().build();
        when(mockRestTemplate.postForEntity(expectedUrl, httpEntityForRestCall(assessment), String.class)).thenReturn(new ResponseEntity<>("Good job!", OK));

        service.acceptAssessmentInvitation(123L, assessment);

        verify(mockRestTemplate).postForEntity(expectedUrl, httpEntityForRestCall(assessment), String.class);
    }

    @Test
    public void test_getOneByProcessRole() {

        String expectedUrl = dataServicesUrl + assessmentRestURL + "/findAssessmentByProcessRole/123";
        Assessment assessment = newAssessment().build();
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Assessment.class)).thenReturn(new ResponseEntity<>(assessment, OK));

        Assessment response = service.getOneByProcessRole(123L);
        assertNotNull(response);
        assertEquals(assessment, response);
    }

    @Test
    public void test_getTotalAssignedByAssessorAndCompetition() {
        String expectedUrl = dataServicesUrl + assessmentRestURL + "/totalAssignedAssessmentsByCompetition/123/456";
        Integer result = 321;
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Integer.class)).thenReturn(new ResponseEntity<>(result, OK));

        Integer response = service.getTotalAssignedByAssessorAndCompetition(123L, 456L);
        assertNotNull(response);
        assertEquals(result, response);
    }

    @Test
    public void test_rejectAssessmentInvitation() {

        String expectedUrl = dataServicesUrl + assessmentRestURL + "/rejectAssessmentInvitation/123";
        ProcessOutcome processOutcomeToUpdate = newProcessOutcome().build();

        when(mockRestTemplate.postForEntity(expectedUrl, httpEntityForRestCall(processOutcomeToUpdate), String.class)).thenReturn(new ResponseEntity<>("", OK));

        service.rejectAssessmentInvitation(123L, processOutcomeToUpdate);
        verify(mockRestTemplate).postForEntity(expectedUrl, httpEntityForRestCall(processOutcomeToUpdate), String.class);
    }

    @Test
    public void test_respondToAssessmentInvitation() {

        String expectedUrl = dataServicesUrl + assessmentRestURL + "/respondToAssessmentInvitation/";
        ObjectNode toUpdate = new ObjectMapper().createObjectNode().
                put("assessorId", 123L).
                put("applicationId", 456L).
                put("decision", Boolean.TRUE).
                put("reason", "Because it's awesome").
                put("observations", "Some observations");

        when(mockRestTemplate.postForEntity(expectedUrl, httpEntityForRestCall(toUpdate), Boolean.class)).thenReturn(new ResponseEntity<>(Boolean.TRUE, OK));

        assertTrue(service.respondToAssessmentInvitation(123L, 456L, true, "Because it's awesome", "Some observations"));
    }

    @Test
    public void test_saveAssessmentSummary() {

        String expectedUrl = dataServicesUrl + assessmentRestURL + "/saveAssessmentSummary/";
        ObjectNode toUpdate = new ObjectMapper().createObjectNode().
                put("assessorId", 123L).
                put("applicationId", 456L).
                put("suitableValue", "Very suitable value").
                put("suitableFeedback", "Nice feedback").
                put("comments", "Some comments");

        when(mockRestTemplate.postForEntity(expectedUrl, httpEntityForRestCall(toUpdate), Boolean.class)).thenReturn(new ResponseEntity<>(Boolean.TRUE, OK));

        assertTrue(service.saveAssessmentSummary(123L, 456L, "Very suitable value", "Nice feedback", "Some comments"));
    }

    @Test
    public void test_submitAssessments() {

        Set<Long> assessmentIdsToUpdate = new HashSet<>();
        assessmentIdsToUpdate.add(456L);
        assessmentIdsToUpdate.add(789L);

        String expectedUrl = dataServicesUrl + assessmentRestURL + "/submitAssessments/";
        ObjectNode toUpdate = new ObjectMapper().createObjectNode().
                put("assessorId", 123L);

        ArrayNode assessmentsToSubmit = new ObjectMapper().valueToTree(assessmentIdsToUpdate);
        toUpdate.putArray("assessmentsToSubmit").addAll(assessmentsToSubmit);

        when(mockRestTemplate.postForEntity(expectedUrl, httpEntityForRestCall(toUpdate), Boolean.class)).thenReturn(new ResponseEntity<>(Boolean.TRUE, OK));

        assertTrue(service.submitAssessments(123L, assessmentIdsToUpdate));
    }
}
