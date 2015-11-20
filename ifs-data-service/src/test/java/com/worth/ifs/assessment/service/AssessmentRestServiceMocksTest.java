package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceMocksTest;
import com.worth.ifs.assessment.domain.Assessment;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
public class AssessmentRestServiceMocksTest extends BaseRestServiceMocksTest<AssessmentRestServiceImpl> {

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
        ResponseEntity<Assessment[]> mockResponse = new ResponseEntity<>(assessments.toArray(new Assessment[]{}), OK);
        when(mockRestTemplate.exchange(eq(dataServicesUrl + assessmentRestURL + "/findAssessmentsByCompetition/123/456"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Assessment[].class))).thenReturn(mockResponse);

        List<Assessment> results = service.getAllByAssessorAndCompetition(assessorId, competitionId);
        assertEquals(3, results.size());
        forEachWithIndex(results, (i, result) -> assertEquals(assessments.get(i), result));
    }

    @Test
    public void test_acceptAssessmentInvitation() {

        String expectedUrl = dataServicesUrl + assessmentRestURL + "/acceptAssessmentInvitation/123/456";
        Assessment assessment = newAssessment().build();
        when(mockRestTemplate.postForEntity(expectedUrl, httpEntityForRestCall(assessment), String.class)).thenReturn(new ResponseEntity<String>("Good job!", OK));

        service.acceptAssessmentInvitation(123L, 456L, assessment);

        verify(mockRestTemplate).postForEntity(expectedUrl, httpEntityForRestCall(assessment), String.class);
    }
}
