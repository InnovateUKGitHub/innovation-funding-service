package com.worth.ifs.assessment.controller;

import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionsRepository;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.workflow.controller.AssessmentProcessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by nunoalexandre on 16/09/15.
 */
@RestController
@RequestMapping("/assessment")
public class AssessmentController {

    @Autowired
    CompetitionsRepository competitions;

    @Autowired
    AssessmentProcessHandler processHandler;

    @Autowired
    AssessmentHandler assessmentHandler;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ApplicationRepository applicationRepository;




    @RequestMapping("/findAssessmentsByCompetition/{userId}/{competitionId}")
    public List<Assessment> findAssessmentsByCompetition( @PathVariable("userId") final Long userId, @PathVariable("competitionId") final Long competitionId ) {
        return assessmentHandler.getAllByCompetitionAndUser(competitionId, userId);
    }

    @RequestMapping("/totalAssignedAssessmentsByCompetition/{userId}/{competitionId}")
    public Integer getTotalAssignedAssessmentsByCompetition( @PathVariable("userId") final Long userId, @PathVariable("competitionId") final Long competitionId ) {
       return assessmentHandler.getTotalAssignedAssessmentsByCompetition(competitionId, userId);
    }

    @RequestMapping("/totalSubmittedAssessmentsByCompetition/{userId}/{competitionId}")
    public Integer getTotalSubmittedAssessmentsByCompetition( @PathVariable("userId") final Long userId, @PathVariable("competitionId") final Long competitionId ) {
        return assessmentHandler.getTotalSubmittedAssessmentsByCompetition(competitionId, userId);
    }







}
