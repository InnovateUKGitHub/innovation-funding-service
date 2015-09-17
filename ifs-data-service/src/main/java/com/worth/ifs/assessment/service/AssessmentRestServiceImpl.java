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


    public List<Assessment> getAssessmentsByCompetition(Long userId, Long competitionId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Assessment[]> responseEntity = restTemplate.getForEntity(dataRestServiceURL + assessmentRestURL + "/findAssessmentsByCompetition/" + userId + "/"+competitionId , Assessment[].class);
        Assessment[] assessments = responseEntity.getBody();

        return Arrays.asList(assessments);
    }


    public Long getTotalAssignedAssessmentsByCompetition(Long userId, Long competitionId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(dataRestServiceURL + assessmentRestURL + "/totalAssignedAssessmentsByCompetition/" + userId + "/"+competitionId , Integer.class);
        Integer number = responseEntity.getBody();

        return new Long(number);
    }


    public Integer getTotalSubmittedAssessmentsByCompetition(Long userId, Long competitionId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Integer>  responseEntity = restTemplate.getForEntity(dataRestServiceURL + assessmentRestURL + "/totalSubmittedAssessmentsByCompetition/" + userId + "/"+competitionId , Integer.class);
        Integer number = responseEntity.getBody();

        return number;
    }





}
