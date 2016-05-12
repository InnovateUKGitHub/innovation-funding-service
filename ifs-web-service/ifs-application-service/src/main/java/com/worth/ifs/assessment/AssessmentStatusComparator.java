package com.worth.ifs.assessment;

import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.assessment.resource.AssessmentResource;

/**
 * Comparator for the assessment status, uses the ordinal attribute to compare, used for sorting the assessments.
 */
public class AssessmentStatusComparator implements java.util.Comparator<AssessmentResource> {
    @Override
    public int compare(AssessmentResource assessment1, AssessmentResource assessment2) {
        AssessmentStates assessmentStates1 = AssessmentStates.getByState(assessment1.getStatus());
        AssessmentStates assessmentStates2 = AssessmentStates.getByState(assessment2.getStatus());
        int result = 0;
        if(assessmentStates1!=null && assessmentStates2!=null) {
            result = Integer.compare(AssessmentStates.getByState(assessment1.getStatus()).getOrdinal(),
                    AssessmentStates.getByState(assessment2.getStatus()).getOrdinal());
        }

        if(result == 0) {
            result = assessment1.getId().compareTo(assessment2.getId());
        }
        return result;
    }
}
