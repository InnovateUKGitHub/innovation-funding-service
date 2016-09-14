package com.worth.ifs.assessment;

import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.assessment.resource.AssessmentResource;

/**
 * Comparator for the assessment status, uses the ordinal attribute to compare, used for sorting the assessments.
 */
public class AssessmentStatusComparator implements java.util.Comparator<AssessmentResource> {
    @Override
    public int compare(AssessmentResource assessment1, AssessmentResource assessment2) {
        AssessmentStates assessmentStates1 = assessment1.getAssessmentState();
        AssessmentStates assessmentStates2 = assessment2.getAssessmentState();
        int result = 0;
        if(assessmentStates1!=null && assessmentStates2!=null) {
            result = Integer.compare(assessmentStates1.ordinal(), assessmentStates2.ordinal());
        }

        if(result == 0) {
            result = assessment1.getId().compareTo(assessment2.getId());
        }
        return result;
    }
}
