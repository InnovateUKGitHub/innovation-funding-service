package com.worth.ifs.assessment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.assessment.domain.Recommendation;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * AssessmentRestServiceImpl is a utility for CRUD operations on {@link Recommendation}.
 * This class connects to the {@link com.worth.ifs.assessment.controller.AssessmentController}
 * through a REST call.
 */
@Service
public class AssessmentRestServiceImpl extends BaseRestService implements AssessmentRestService {

    @Value("${ifs.data.service.rest.assessment}")
    String assessmentRestURL;


    public List<Recommendation> getAllByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return Arrays.asList(restGet(assessmentRestURL +"/findAssessmentsByCompetition/" + assessorId + "/" + competitionId , Recommendation[].class));
    }

    public Recommendation getOneByAssessorAndApplication(Long assessorId, Long applicationId) {
        return restGet(assessmentRestURL +"/findAssessmentByApplication/" + assessorId + "/" + applicationId, Recommendation.class);
    }


    public Integer getTotalAssignedByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return restGet(assessmentRestURL +"/totalAssignedAssessmentsByCompetition/" + assessorId + "/" + competitionId, Integer.class);
    }


    public Integer getTotalSubmittedByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return restGet(assessmentRestURL +"/totalSubmittedAssessmentsByCompetition/" + assessorId + "/" + competitionId, Integer.class);
    }

    public Boolean respondToAssessmentInvitation(Long assessorId, Long applicationId, Boolean decision, String reason, String observations) {

        //builds the node with the response form fields data
        ObjectNode node =  new ObjectMapper().createObjectNode();
        node.put("assessorId", assessorId);
        node.put("applicationId", applicationId);
        node.put("decision", decision);
        node.put("reason", reason);
        node.put("observations", HtmlUtils.htmlEscape(observations));

        return restPost(assessmentRestURL +"/respondToAssessmentInvitation/", node, Boolean.class);
    }

    public Boolean saveAssessmentSummary(Long assessorId, Long applicationId, String suitableValue, String suitableFeedback, String comments, Double overallScore) {
        //builds the node with the response form fields data
        ObjectNode node =  new ObjectMapper().createObjectNode();
        node.put("assessorId", assessorId);
        node.put("applicationId", applicationId);
        node.put("suitableValue", suitableValue);
        node.put("overallScore", overallScore);
        node.put("suitableFeedback", HtmlUtils.htmlEscape(suitableFeedback));
        node.put("comments",  HtmlUtils.htmlEscape(comments));
        return restPost(assessmentRestURL +"/saveAssessmentSummary/", node, Boolean.class);
    }

    public Boolean submitAssessments(Long assessorId, Set<Long> assessmentIds ) {
        //builds the node with the response form fields data
        ObjectNode node =  new ObjectMapper().createObjectNode();
        node.put("assessorId", assessorId);
        ArrayNode assessmentsToSubmit = new ObjectMapper().valueToTree(assessmentIds);
        node.putArray("assessmentsToSubmit").addAll(assessmentsToSubmit);
        return restPost(assessmentRestURL +"/submitAssessments/", node,  Boolean.class);
    }

    @Override
    public void acceptAssessmentInvitation(Long applicationId, Long assessorId, Recommendation recommendation) {
        restPost(assessmentRestURL + "/acceptAssessmentInvitation/" + applicationId + "/" + assessorId, recommendation, String.class);
    }

    @Override
    public void rejectAssessmentInvitation(Long applicationId, Long assessorId, Recommendation recommendation) {
        restPost(assessmentRestURL + "/rejectAssessmentInvitation/" + applicationId + "/" + assessorId, recommendation, String.class);
    }

}
