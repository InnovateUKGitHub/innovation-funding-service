package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.User;
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

    public List<Assessment> getAllByCompetitionAndUser(Competition competition, User user ) {
        return assessments.findByProcessAssessorAndProcessApplicationCompetition(user, competition);
    }
    public List<Assessment> getAssessmentsOfAssessor(User assessor) {
            return assessments.findByProcessAssessor(assessor);
    }
    public Integer getTotalSubmittedAssessmentsByCompetition(Competition competition, User user) {
        return assessments.findSubmittedAssessmentsByCompetition(user, competition).size();
    }


}
