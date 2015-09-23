package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.domain.Assessment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * Created by nunoalexandre on 16/09/15.
 */
@RestController
@RequestMapping("/assessment")
public class AssessmentController {

    @Autowired
    AssessmentHandler assessmentHandler;


    @RequestMapping("/findAssessmentsByCompetition/{userId}/{competitionId}")
    public Set<Assessment> findAssessmentsByCompetition( @PathVariable("userId") final Long userId, @PathVariable("competitionId") final Long competitionId ) {
        return assessmentHandler.getAllByCompetitionAndUser(competitionId, userId);
    }

    @RequestMapping("/findAssessmentByApplication/{userId}/{applicationId}")
    public Assessment getAssessmentByUserAndApplication( @PathVariable("userId") final Long userId, @PathVariable("applicationId") final Long applicationId ) {
        return assessmentHandler.getOneByAssessorAndApplication(userId, applicationId);
    }

    @RequestMapping("/totalAssignedAssessmentsByCompetition/{userId}/{competitionId}")
    public Integer getTotalAssignedAssessmentsByCompetition( @PathVariable("userId") final Long userId, @PathVariable("competitionId") final Long competitionId ) {
       return assessmentHandler.getTotalAssignedAssessmentsByCompetition(competitionId, userId);
    }

    @RequestMapping("/totalSubmittedAssessmentsByCompetition/{userId}/{competitionId}")
    public Integer getTotalSubmittedAssessmentsByCompetition( @PathVariable("userId") final Long userId, @PathVariable("competitionId") final Long competitionId ) {
        return assessmentHandler.getTotalSubmittedAssessmentsByCompetition(competitionId, userId);
    }


    /** this two are needed to avoid 404 in case the user doesnt specify any observations **/
    @RequestMapping("/respondToAssessmentInvitation/{userId}/{applicationId}/{decision}/{decisionReason}/{observations}")
    public Boolean respondToAssessmentInvitation( @PathVariable("userId") final Long userId,
                                                  @PathVariable("applicationId") final Long applicationId,
                                                  @PathVariable("decision") final Boolean decision,
                                                  @PathVariable("decisionReason") final String decisionReason,
                                                  @PathVariable("observations") final String observations)
    {
        return assessmentHandler.respondToAssessmentInvitation(userId, applicationId, decision, decisionReason, observations);
    }
    @RequestMapping("/respondToAssessmentInvitation/{userId}/{applicationId}/{decision}/{decisionReason}/")
    public Boolean respondToAssessmentInvitationSafe( @PathVariable("userId") final Long userId,
                                                  @PathVariable("applicationId") final Long applicationId,
                                                  @PathVariable("decision") final Boolean decision,
                                                  @PathVariable("decisionReason") final String decisionReason)
    {
        return assessmentHandler.respondToAssessmentInvitation(userId, applicationId, decision, decisionReason, "");
    }


}
