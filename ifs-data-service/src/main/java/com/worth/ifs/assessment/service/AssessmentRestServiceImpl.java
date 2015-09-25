package com.worth.ifs.assessment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.commons.service.BaseRestServiceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * AssessmentRestRestServiceIpml is a utility to use client-side to retrieve Assessment data from the data-service controllers.
 */

@Service
public class AssessmentRestServiceImpl extends BaseRestServiceProvider implements AssessmentRestService {

    @Value("${ifs.data.service.rest.assessment}")
    String assessmentRestURL;


    public Set<Assessment> getAllByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return new LinkedHashSet<>(Arrays.asList(restCall("/findAssessmentsByCompetition/" + assessorId + "/" + competitionId , Assessment[].class)));
    }

    public Assessment getOneByAssessorAndApplication(Long assessorId, Long applicationId) {
        return restCall("/findAssessmentByApplication/" + assessorId + "/" + applicationId , Assessment.class);
    }


    public Integer getTotalAssignedByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return restCall("/totalAssignedAssessmentsByCompetition/" + assessorId + "/" + competitionId , Integer.class);
    }


    public Integer getTotalSubmittedByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return restCall("/totalSubmittedAssessmentsByCompetition/" + assessorId + "/" + competitionId , Integer.class);
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

    @Override
    protected  <T> T restCall(String path, Class c) {
        return super.restCall(assessmentRestURL + path,c);
    }

    @Override
    protected  <T> T restPost(String message, String path, Class c) {
        return super.restPost(message, assessmentRestURL + path, c);
    }



}
