package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.workflow.domain.ProcessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by nunoalexandre on 16/09/15.
 */
@Component
public class AssessmentHandler {

    @Autowired
    private AssessmentRepository assessments;


    public AssessmentHandler(){}

    public void save(Assessment a) {
        assessments.save(a);
    }

    public Assessment saveAndGet(Assessment a) {
        return assessments.save(a);
    }

    public Assessment getOne(Long id) {
        return assessments.findById(id);
    }

    public List<Assessment> getAllByCompetitionAndUser(Long competitionId, Long userId) {
        return assessments.findByAssessorAndCompetition(userId, competitionId);
    }
    public List<Assessment> getAssessmentsOfAssessor(Long assessorId) {
            return assessments.findByProcessAssessorId(assessorId);
    }
    public Integer getTotalSubmittedAssessmentsByCompetition(Long competitionId, Long userId) {
        return assessments.findNumberOfSubmittedAssessmentsByCompetition(userId, competitionId);
    }
    public Integer getTotalAssignedAssessmentsByCompetition(Long competitionId, Long userId) {
        // By 'assigned' is meant an assessment with an open process, so all but ProcessStatus.REJECTED.
        return assessments.findNumberOfAssignedAssessmentsByCompetition(userId, competitionId, ProcessStatus.REJECTED);
    }


}
