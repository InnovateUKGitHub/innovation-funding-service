package com.worth.ifs.assessment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.assessmentResourceListType;
import static com.worth.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

        List<AssessmentResource> assessments = newAssessmentResource().build(3);
        setupGetWithRestResultExpectations(assessmentRestURL + "/findAssessmentsByCompetition/123/456", assessmentResourceListType(), assessments);

        List<AssessmentResource> results = service.getAllByAssessorAndCompetition(assessorId, competitionId).getSuccessObject();
        assertEquals(3, results.size());
        forEachWithIndex(results, (i, result) -> assertEquals(assessments.get(i), result));
    }

    @Test
    public void test_acceptAssessmentInvitation() {

        AssessmentResource assessment = newAssessmentResource().build();
        setupPostWithRestResultExpectations(assessmentRestURL + "/acceptAssessmentInvitation/123", assessment, OK);

        assertTrue(service.acceptAssessmentInvitation(123L, assessment).isSuccess());
        setupPostWithRestResultVerifications(assessmentRestURL + "/acceptAssessmentInvitation/123", Void.class, assessment);
    }

    @Test
    public void test_getOneByProcessRole() {

        AssessmentResource assessment = newAssessmentResource().build();
        setupGetWithRestResultExpectations(assessmentRestURL + "/findAssessmentByProcessRole/123", AssessmentResource.class, assessment);

        AssessmentResource response = service.getOneByProcessRole(123L).getSuccessObject();
        assertEquals(assessment, response);
    }

    @Test
    public void test_getTotalAssignedByAssessorAndCompetition() {
        Integer result = 321;
        setupGetWithRestResultExpectations(assessmentRestURL + "/totalAssignedAssessmentsByCompetition/123/456", Integer.class, result);

        Integer response = service.getTotalAssignedByAssessorAndCompetition(123L, 456L).getSuccessObject();
        assertEquals(result, response);
    }

    @Test
    public void test_rejectAssessmentInvitation() {

        ProcessOutcomeResource processOutcomeToUpdate = newProcessOutcomeResource().build();
        setupPostWithRestResultExpectations(assessmentRestURL + "/rejectAssessmentInvitation/123", processOutcomeToUpdate, OK);

        service.rejectAssessmentInvitation(123L, processOutcomeToUpdate);
        setupPostWithRestResultVerifications(assessmentRestURL + "/rejectAssessmentInvitation/123", Void.class, processOutcomeToUpdate);
    }

    @Test
    public void test_respondToAssessmentInvitation() {

        ObjectNode toUpdate = new ObjectMapper().createObjectNode().
                put("assessorId", 123L).
                put("applicationId", 456L).
                put("decision", Boolean.TRUE).
                put("reason", "Because it's awesome").
                put("observations", "Some observations");

        setupPostWithRestResultExpectations(assessmentRestURL + "/respondToAssessmentInvitation/", toUpdate, OK);

        assertTrue(service.respondToAssessmentInvitation(123L, 456L, true, "Because it's awesome", "Some observations").isSuccess());

        setupPostWithRestResultVerifications(assessmentRestURL + "/respondToAssessmentInvitation/", Void.class, toUpdate);
    }

    @Test
    public void test_saveAssessmentSummary() {

        ObjectNode toUpdate = new ObjectMapper().createObjectNode().
                put("assessorId", 123L).
                put("applicationId", 456L).
                put("suitableValue", "Very suitable value").
                put("suitableFeedback", "Nice feedback").
                put("comments", "Some comments");

        setupPostWithRestResultExpectations(assessmentRestURL + "/saveAssessmentSummary/", toUpdate, OK);

        assertTrue(service.saveAssessmentSummary(123L, 456L, "Very suitable value", "Nice feedback", "Some comments").isSuccess());

        setupPostWithRestResultVerifications(assessmentRestURL + "/saveAssessmentSummary/", Void.class, toUpdate);
    }

    @Test
    public void test_submitAssessments() {

        Set<Long> assessmentIdsToUpdate = new HashSet<>();
        assessmentIdsToUpdate.add(456L);
        assessmentIdsToUpdate.add(789L);

        ObjectNode toUpdate = new ObjectMapper().createObjectNode().
                put("assessorId", 123L);

        ArrayNode assessmentsToSubmit = new ObjectMapper().valueToTree(assessmentIdsToUpdate);
        toUpdate.putArray("assessmentsToSubmit").addAll(assessmentsToSubmit);

        setupPostWithRestResultExpectations(assessmentRestURL + "/submitAssessments/", toUpdate, OK);

        assertTrue(service.submitAssessments(123L, assessmentIdsToUpdate).isSuccess());

        setupPostWithRestResultVerifications(assessmentRestURL + "/submitAssessments/", Void.class, toUpdate);
    }
}
