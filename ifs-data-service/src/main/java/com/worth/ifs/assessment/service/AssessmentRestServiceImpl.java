package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.commons.service.BaseRestServiceProvider;
import com.worth.ifs.competition.service.CompetitionsRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * AssessmentRestRestServiceIpml is a utility to use client-side to retrieve Assessment data from the data-service controllers.
 */

@Service
public class AssessmentRestServiceImpl extends BaseRestServiceProvider implements AssessmentRestService {

    @Value("${ifs.data.service.rest.assessment}")
    String assessmentRestURL;


    public List<Assessment> getAllByAssessorAndCompetition(Long assessorId, Long competitionId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Assessment[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + assessmentRestURL + "/findAssessmentsByCompetition/" + assessorId + "/" + competitionId , Assessment[].class);
        Assessment[] assessments = responseEntity.getBody();

        return Arrays.asList(assessments);
    }


    public Assessment getOneByAssessorAndApplication(Long assessorId, Long applicationId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Assessment> responseEntity = restTemplate.getForEntity(dataRestServiceURL + assessmentRestURL + "/findAssessmentByApplication/" + assessorId + "/" + applicationId , Assessment.class);
        Assessment assessment = responseEntity.getBody();

        return assessment;
    }


    public Integer getTotalAssignedByAssessorAndCompetition(Long assessorId, Long competitionId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(dataRestServiceURL + assessmentRestURL + "/totalAssignedAssessmentsByCompetition/" + assessorId + "/" + competitionId , Integer.class);
        Integer totalAssigned = responseEntity.getBody();

        return totalAssigned;
    }


    public Integer getTotalSubmittedByAssessorAndCompetition(Long assessorId, Long competitionId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Integer>  responseEntity = restTemplate.getForEntity(dataRestServiceURL + assessmentRestURL + "/totalSubmittedAssessmentsByCompetition/" + assessorId + "/" + competitionId , Integer.class);
        Integer totalSubmitted = responseEntity.getBody();

        return totalSubmitted;
    }





}
