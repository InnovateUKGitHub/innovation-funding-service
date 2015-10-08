package com.worth.ifs.assessment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.commons.service.BaseRestServiceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * AssessmentRestServiceImpl is a utility for CRUD operations on {@link Assessment}.
 * This class connects to the {@link com.worth.ifs.assessment.controller.AssessmentController}
 * through a REST call.
 */
@Service
public class AssessmentRestServiceImpl extends BaseRestServiceProvider implements AssessmentRestService {

    @Value("${ifs.data.service.rest.assessment}")
    String assessmentRestURL;


    public List<Assessment> getAllByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return Arrays.asList(restGet("/findAssessmentsByCompetition/" + assessorId + "/" + competitionId , Assessment[].class));
    }

    public Assessment getOneByAssessorAndApplication(Long assessorId, Long applicationId) {
        return restGet("/findAssessmentByApplication/" + assessorId + "/" + applicationId, Assessment.class);
    }


    public Integer getTotalAssignedByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return restGet("/totalAssignedAssessmentsByCompetition/" + assessorId + "/" + competitionId, Integer.class);
    }


    public Integer getTotalSubmittedByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return restGet("/totalSubmittedAssessmentsByCompetition/" + assessorId + "/" + competitionId, Integer.class);
    }

    public Boolean respondToAssessmentInvitation(Long assessorId, Long applicationId, Boolean decision, String reason, String observations) {

        //builds the node with the response form fields data
        ObjectNode node =  new ObjectMapper().createObjectNode();
        node.put("assessorId", assessorId);
        node.put("applicationId", applicationId);
        node.put("decision", decision);
        node.put("reason", reason);
        node.put("observations", HtmlUtils.htmlEscape(observations));

        return restPost(node.toString(), "/respondToAssessmentInvitation/", Boolean.class);
    }

    public Boolean submitAssessments(Long assessorId, Set<Long> assessmentIds ) {

        //builds the node with the response form fields data
        ObjectNode node =  new ObjectMapper().createObjectNode();
        node.put("assessorId", assessorId);
        ArrayNode assessmentsToSubmit =  new ObjectMapper().valueToTree(assessmentIds);
        node.putArray("assessmentsToSubmit").addAll(assessmentsToSubmit);

        return restPost(node.toString(), "/submitAssessments/", Boolean.class);

    }

    public Boolean saveAssessmentSummary(Long assessorId, Long applicationId, String suitableValue, String suitableFeedback, String comments, Double overallScore) {

        System.out.println("AssessmentRestImp > saveAssessmentSummary ");

        //builds the node with the response form fields data
        ObjectNode node =  new ObjectMapper().createObjectNode();
        node.put("assessorId", assessorId);
        node.put("applicationId", applicationId);
        node.put("suitableValue", suitableValue);
        node.put("overallScore", overallScore);

        node.put("suitableFeedback", HtmlUtils.htmlEscape(suitableFeedback));
        System.out.println("AssessmentRestImp > saveAssessmentSummary before comment ");

        node.put("comments",  HtmlUtils.htmlEscape(comments));
        System.out.println("AssessmentRestImp > saveAssessmentSummary after comment ");


        System.out.println("node.toString() is " + node.toString());

        return restPost(node.toString(), "/saveAssessmentSummary/", Boolean.class);
    }

    @Override
    public void acceptAssessmentInvitation(Long applicationId, Long assessorId, Assessment assessment) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + assessmentRestURL + "/acceptAssessmentInvitation/" + applicationId + "/" + assessorId;

        HttpEntity<Assessment> entity = new HttpEntity<>(assessment, getJSONHeaders());
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @Override
    public void rejectAssessmentInvitation(Long applicationId, Long assessorId, Assessment assessment) {
        RestTemplate restTemplate = new RestTemplate();
        String url = dataRestServiceURL + assessmentRestURL + "/rejectAssessmentInvitation/" + applicationId + "/" + assessorId;

        HttpEntity<Assessment> entity = new HttpEntity<>(assessment, getJSONHeaders());
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @Override
    protected  <T> T restGet(String path, Class c) {
        return super.restGet(assessmentRestURL + path, c);
    }

    @Override
    protected  <T> T restPost(String message, String path, Class c) {
        return super.restPost(message, assessmentRestURL + path, c);
    }



}
