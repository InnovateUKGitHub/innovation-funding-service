package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.commons.service.BaseRestServiceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public Boolean respondToAssessmentInvitation(Long assessorId, Long applicationId, Boolean decision, String decisionReason, String observations) {
        return restCall("/respondToAssessmentInvitation/" + assessorId + "/" + applicationId + "/" + decision
                + "/" + decisionReason + "/" + observations , Boolean.class);
    }

    @Override
    protected  <T> T restCall(String path, Class c) {
        return super.restCall(assessmentRestURL + path,c);
    }



}
