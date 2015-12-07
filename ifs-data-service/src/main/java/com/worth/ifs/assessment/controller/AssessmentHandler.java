package com.worth.ifs.assessment.controller;

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

    /**
     * Get's all the assessments by competition and assessor.
     * By 'All' is meant all the assessments whose invitation was not rejected.
     * Also, groups the assessments by first having the pending ones and only after the open/active/submitted.
     */
    public List<Assessment> getAllByProcessRole(Long processRoleId) {
        Set<String> states = AssessmentStates.getStates();
        states.remove(AssessmentStates.REJECTED.getState());
        return assessments.findByProcessRoleIdAndStatusIn(processRoleId, states);
    }

    public Assessment getOneByProcessRole(Long processRoleId) {
        return assessments.findOneByProcessRoleId(processRoleId);
    }

    public Integer getTotalSubmittedAssessments(Long processRoleId) {
        return assessments.countByProcessRoleIdAndStatus(processRoleId, "submitted");
    }
    public Integer getTotalAssignedAssessmentsByProcessRole(Long processRoleId) {
        // By 'assigned' is meant an assessment process not rejected
        return assessments.countByProcessRoleIdAndNotStatus(processRoleId, "rejected");
    }

    public RecommendedValue getRecommendedValueFromString(String value) {

        if ( value.equals("yes") )
            return RecommendedValue.YES;
        else  if ( value.equals("no"))
            return RecommendedValue.NO;
        else
            return RecommendedValue.EMPTY;

    }
}
