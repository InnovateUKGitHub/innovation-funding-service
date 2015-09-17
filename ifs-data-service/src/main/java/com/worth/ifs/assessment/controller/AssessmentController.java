package com.worth.ifs.assessment.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionsRepository;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.workflow.controller.AssessmentProcessHandler;
import com.worth.ifs.workflow.domain.AssessmentProcess;
import com.worth.ifs.workflow.domain.ProcessStatus;
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




    @RequestMapping("/findAssessmentsByCompetition/{uid}/{cid}")
    public List<Assessment> findAssessmentsByCompetition( @PathVariable("uid") final Long uId, @PathVariable("cid") final Long cid ) {

        User assessor = userRepository.findOne(uId);
        Competition comp = competitions.findById(cid);


        List<Assessment> assessments = null;

        try {
            assessments = assessmentHandler.getAllByCompetitionAndUser(comp, assessor);
            System.out.println("Assessments of competition 1 for user 3 are: " + assessments.size());

        } catch (Exception e) {
            System.out.println("Exception trying to  assessmentHandler.save(assessment)");
        }

        return assessments;
    }

    @RequestMapping("/totalAssignedAssessmentsByCompetition/{uid}/{cid}")
    public Integer getTotalAssignedAssessmentsByCompetition( @PathVariable("uid") final Long uId, @PathVariable("cid") final Long cid ) {
        List<Assessment> assessments = findAssessmentsByCompetition(uId,cid);

        return assessments == null ? 0 : assessments.size();
    }

    @RequestMapping("/totalSubmittedAssessmentsByCompetition/{uid}/{cid}")
    public Integer getTotalSubmittedAssessmentsByCompetition( @PathVariable("uid") final Long uId, @PathVariable("cid") final Long cid ) {

        User assessor = userRepository.findOne(uId);
        Competition comp = competitions.findById(cid);

        return assessmentHandler.getTotalSubmittedAssessmentsByCompetition(comp, assessor);
    }







}
