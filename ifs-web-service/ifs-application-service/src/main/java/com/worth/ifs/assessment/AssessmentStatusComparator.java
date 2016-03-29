package com.worth.ifs.assessment;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentStates;

/**
 * Comparator for the assessment status, uses the ordinal attribute to compare, used for sorting the assessments.
 */
public class AssessmentStatusComparator implements java.util.Comparator<Assessment> {
    @Override
    public int compare(Assessment assessment1, Assessment assessment2) {
        AssessmentStates assessmentStates1 = AssessmentStates.getByState(assessment1.getProcessStatus());
        AssessmentStates assessmentStates2 = AssessmentStates.getByState(assessment2.getProcessStatus());
        int result = 0;
        if(assessmentStates1!=null && assessmentStates2!=null) {
            result = Integer.compare(AssessmentStates.getByState(assessment1.getProcessStatus()).getOrdinal(),
                    AssessmentStates.getByState(assessment2.getProcessStatus()).getOrdinal());
        }

        if(result == 0) {
            result = assessment1.getId().compareTo(assessment2.getId());
        }
        return result;
    }
}
