package com.worth.ifs.assessment.controller;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentStates;
import com.worth.ifs.assessment.domain.RecommendedValue;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * AssessmentHandler is responsible to manage the domain logic around the Assessment's domain range.
 * This avoids code coupling and spread knowledge and responsability over Assessment's and allows us
 * to have the access to them centered here, preventing any incoherence by multiple ways.
 *
 * So this class is responsible to manage interactions with the AssessmentRepository, being a facade between
 * the outside world and the Assessment's world.
 */
@Component
public class AssessmentHandler {

    @Autowired
    private AssessmentRepository assessmentRepository;

    public AssessmentHandler(){}

    public void save(Assessment a) {
        assessmentRepository.save(a);
    }

    public Assessment saveAndGet(Assessment a) {
        return assessmentRepository.save(a);
    }

    public Assessment getOne(Long id) {
        return assessmentRepository.findById(id);
    }

    /**
     * Get's all the assessments by competition and assessor.
     * By 'All' is meant all the assessments whose invitation was not rejected.
     * Also, groups the assessments by first having the pending ones and only after the open/active/submitted.
     */
    public List<Assessment> getAllByCompetitionAndAssessor(Long competitionId, Long assessorId) {
        Set<String> states = AssessmentStates.getStates();
        states.remove(AssessmentStates.REJECTED.getState());
        return assessmentRepository.findByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatusIn(assessorId, competitionId, states);
    }

    public Assessment getOneByProcessRole(Long processRoleId) {
        return assessmentRepository.findOneByProcessRoleId(processRoleId);
    }

    public Integer getTotalSubmittedAssessmentsByCompetition(Long competitionId, Long assessorId) {
        return assessmentRepository.countByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatus(assessorId, competitionId, ApplicationStatusConstants.SUBMITTED.getName());
    }
    public Integer getTotalAssignedAssessmentsByCompetition(Long competitionId, Long assessorId) {
        // By 'assigned' is meant an assessment process not rejected
        return assessmentRepository.countByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatusNot(assessorId, competitionId, ApplicationStatusConstants.REJECTED.getName());
    }

    public RecommendedValue getRecommendedValueFromString(String value) {
        switch (value) {
            case "yes":
                return RecommendedValue.YES;
            case "no":
                return RecommendedValue.NO;
            default:
                return RecommendedValue.EMPTY;
        }
    }
}
